package com.projects.resolver.service.Impl;

import com.projects.resolver.llm.PromptUtils;
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

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">f(.*?)</file>",Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        createChatSessionIfNotExists(projectId, userId);
        // passing advisor to LLM
        Map<String,Object> advisorParams = Map.of(
                "userId",userId,
                "projectId",projectId
        );
        StringBuilder fullResponseBuffer = new StringBuilder();
        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .advisors(
                // advisors: Before entering LLM, advisors will run, for validation, sending extra params
                // advisors are for modying prompt and giving it to llm
                advisorSpec -> {
                    advisorSpec.params(advisorParams);
                })
                .stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                        // buffer responses are combined
                        String content = chatResponse.getResult().getOutput().getText();
                        fullResponseBuffer.append(content);
                })
                .doOnComplete(()->{
                    // async updating to reduce load on current thread
                    Schedulers.boundedElastic().schedule(()->{
                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                    });
                })
                .doOnError(error-> log.error("Error during streaming for project", projectId))
                .map(chatResponse -> Objects.requireNonNull(chatResponse.getResult().getOutput().getText()));
    }

    // to save file response in minio
    private void parseAndSaveFiles(String fullResponse, Long projectId) {
        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
        while(matcher.find()){
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();
            projectFileService.saveFile(projectId, filePath, fileContent);
        }
    }

    private void createChatSessionIfNotExists(Long projectId, Long userId) {

    }
}
