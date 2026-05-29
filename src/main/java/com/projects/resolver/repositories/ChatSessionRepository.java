package com.projects.resolver.repositories;

import com.projects.resolver.entity.ChatSession;
import com.projects.resolver.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
