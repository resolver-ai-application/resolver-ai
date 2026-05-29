package com.projects.resolver.entity;

import com.projects.resolver.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="chat_message")
@Builder
public class ChatMessage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
                    @JoinColumn(name="project_id",referencedColumnName = "project_id", nullable = false),
                    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
            })
    ChatSession chatSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MessageRole role; //USER, ASSISTANT

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sequenceOrder ASC")
    List<ChatEvent> events;

    @Column(columnDefinition = "text")
    String content;

//    String toolCalls;

    Integer tokenUsed =0 ;

    @CreationTimestamp
    Instant createdAt;

}
