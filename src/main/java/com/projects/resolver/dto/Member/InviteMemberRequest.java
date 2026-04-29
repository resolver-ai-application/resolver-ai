package com.projects.resolver.dto.Member;

import com.projects.resolver.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
