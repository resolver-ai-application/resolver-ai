package com.projects.resolver.dto.Member;

import com.projects.resolver.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest( @NotNull ProjectRole role) {
}
