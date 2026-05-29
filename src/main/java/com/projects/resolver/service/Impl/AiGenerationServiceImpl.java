package com.projects.resolver.service.Impl;

import com.projects.resolver.entity.*;
import com.projects.resolver.enums.ChatEventType;
import com.projects.resolver.enums.MessageRole;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import com.projects.resolver.llm.LlmResponseParser;
import com.projects.resolver.llm.PromptUtils;
import com.projects.resolver.llm.advisors.FileTreeContextAdvisor;
import com.projects.resolver.llm.tools.CodeGenerationTools;
import com.projects.resolver.repositories.*;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.AiGenerationService;
import com.projects.resolver.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LlmResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>",Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);
        // passing advisor to LLM
        Map<String,Object> advisorParams = Map.of(
                "userId",userId,
                "projectId",projectId
        );
        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(
                // advisors: Before entering LLM, advisors will run, for validation, sending extra params
                // advisors are for modifying prompt and giving it to llm
                advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                        // buffer responses are combined
                        String content = chatResponse.getResult().getOutput().getText();
                        if(content!=null && !content.isEmpty() && endTime.get()==0){
                            endTime.set(System.currentTimeMillis());
                        }
                        fullResponseBuffer.append(content);
                })
                .doOnComplete(()->{
                    // async updating to reduce load on current thread
                    Schedulers.boundedElastic().schedule(()->{
//                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                        long duration = (endTime.get()-startTime.get())/1000;
                        finalizeChats(userMessage,chatSession, fullResponseBuffer.toString(), duration);
                    });

                })
                .doOnError(error-> log.error("Error during streaming for project", projectId))
                .map(chatResponse -> Objects.requireNonNull(chatResponse.getResult().getOutput().getText()));
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration){
        Long projectId = chatSession.getProject().getId();
        //save user message
        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .build());
        ChatMessage assistantChatMessage = ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .chatSession(chatSession)
                .content("Assistant messages here...")
                .build();
        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText,assistantChatMessage);

        chatEventList.addFirst(ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .chatMessage(assistantChatMessage)
                        .content("Thought for "+duration+"s")
                        .sequenceOrder(0)
                .build());

        chatEventList.stream().filter(e->e.getType()== ChatEventType.FILE_EDIT)
                .forEach(e->projectFileService.saveFile(projectId,e.getFilePath(),e.getContent()));
        chatEventRepository.saveAll(chatEventList);

    }

    // to save file response in minio
//    private void parseAndSaveFiles(String fullResponse, Long projectId) {
//        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
//        while(matcher.find()){
//            String filePath = matcher.group(1);
//            String fileContent = matcher.group(2).trim();
//            projectFileService.saveFile(projectId, filePath, fileContent);
//        }
//    }

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);
        if(chatSession==null){
            Project project = projectRepository.findById(projectId).orElseThrow(
                    ()-> new ResourceNotFoundException("Project",projectId.toString())
            );
            User user = userRepository.findById(userId).orElseThrow(
                    ()-> new ResourceNotFoundException("User",userId.toString())
            );
            chatSession = ChatSession.builder().id(chatSessionId).project(project).user(user).build();
            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }
}
