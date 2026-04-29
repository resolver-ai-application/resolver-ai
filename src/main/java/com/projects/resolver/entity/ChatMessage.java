package com.projects.resolver.entity;

import com.projects.resolver.enums.MessageRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_message")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {

    Long id;
    ChatSession chatSession;
    String content;
    String toolCalls;
    Integer tokenUsed;
    Instant createdAt;
    MessageRole messageRole;
}
