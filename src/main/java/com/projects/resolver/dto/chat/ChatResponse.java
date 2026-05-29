package com.projects.resolver.dto.chat;

import com.projects.resolver.entity.ChatEvent;
import com.projects.resolver.entity.ChatSession;
import com.projects.resolver.enums.MessageRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

public record ChatResponse (
    Long id,
    ChatSession chatSession,
    MessageRole role,
    List<ChatEventResponse> events,// todo
    String content,
    Integer tokenUsed,
    Instant createdAt
){
}
