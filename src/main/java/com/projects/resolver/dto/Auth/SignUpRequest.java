package com.projects.resolver.dto.Auth;

public record SignUpRequest(
        String email,
        String name,
        String password
) {
}
