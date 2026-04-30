package com.projects.resolver.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email @NotBlank String username,
        @NotBlank @Size(min=4, max=50) String password
) {
}
