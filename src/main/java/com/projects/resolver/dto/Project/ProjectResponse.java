package com.projects.resolver.dto.Project;

import com.projects.resolver.dto.Auth.UserProfileResponse;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
