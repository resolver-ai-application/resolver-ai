package com.projects.resolver.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @Email @NotBlank String email,
        @Size(min=1,max=50) String name,
        @Size(min=4) String password
) {
}
