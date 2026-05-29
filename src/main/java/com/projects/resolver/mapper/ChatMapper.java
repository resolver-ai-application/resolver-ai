package com.projects.resolver.mapper;

import com.projects.resolver.dto.chat.ChatResponse;
import com.projects.resolver.entity.ChatMessage;
import jakarta.persistence.ManyToOne;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}

