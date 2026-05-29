package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.chat.ChatResponse;
import com.projects.resolver.entity.ChatMessage;
import com.projects.resolver.entity.ChatSession;
import com.projects.resolver.entity.ChatSessionId;
import com.projects.resolver.mapper.ChatMapper;
import com.projects.resolver.repositories.ChatMessageRepository;
import com.projects.resolver.repositories.ChatSessionRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = chatSessionRepository.getReferenceById(new ChatSessionId(projectId,userId));
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
