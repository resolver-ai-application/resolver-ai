package com.projects.resolver.controller;

import com.projects.resolver.dto.chat.ChatRequest;
import com.projects.resolver.dto.chat.ChatResponse;
import com.projects.resolver.dto.chat.StreamResponse;
import com.projects.resolver.service.AiGenerationService;
import com.projects.resolver.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {

    AiGenerationService aiGenerationService;
    ChatService chatService;

//    @PostMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PostMapping(value = "/api/v1/intelligence/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StreamResponse>> streamChat(
        @RequestBody ChatRequest request){
        return aiGenerationService.streamResponse(request.message(), request.projectId())
                .map(data-> ServerSentEvent.<StreamResponse>builder()
                        .data(data)
                        .build());
    }

//    @GetMapping("/projects/{projectId}")
    @GetMapping("/api/v1/intelligence/chat/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable Long projectId){
        return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
    }
}
