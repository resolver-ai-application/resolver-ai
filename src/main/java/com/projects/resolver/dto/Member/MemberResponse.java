package com.projects.resolver.dto.Member;

import com.projects.resolver.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String email,
        String name,
        String avtarUrl,
        ProjectRole role,
        Instant invitedAt
) {
}
