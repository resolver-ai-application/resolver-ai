package com.projects.resolver.dto.Project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
       @NotBlank String name
) {
}
