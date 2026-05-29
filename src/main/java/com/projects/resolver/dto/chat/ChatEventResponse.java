package com.projects.resolver.dto.chat;

import com.projects.resolver.entity.ChatMessage;
import com.projects.resolver.enums.ChatEventType;
import jakarta.persistence.*;

public record ChatEventResponse(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) {
}
