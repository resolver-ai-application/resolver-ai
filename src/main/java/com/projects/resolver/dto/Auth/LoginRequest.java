package com.projects.resolver.dto.Auth;

public record LoginRequest(
        String email,
        String password
) {
}
