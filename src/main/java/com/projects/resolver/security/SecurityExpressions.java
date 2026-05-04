package com.projects.resolver.security;

import com.projects.resolver.enums.ProjectPermission;
import com.projects.resolver.repositories.ProjectMemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import static com.projects.resolver.enums.ProjectPermission.*;


@Component("security")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SecurityExpressions {

    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;

    public boolean canViewProject(Long projectId) {
        return hasPermission(projectId, VIEW);
    }

    public boolean canEditProject(Long projectId) {
        return hasPermission(projectId, EDIT);
    }

    public boolean canDeleteProject(Long projectId) {
        return hasPermission(projectId, DELETE);
    }

    public boolean canViewMembers(Long projectId) {
        return hasPermission(projectId, VIEW_MEMBERS);
    }

    public boolean canManageMembers(Long projectId) {
        return hasPermission(projectId, MANAGE_MEMBERS);
    }

    private boolean hasPermission(Long projectId, ProjectPermission projectPermission) {
        Long userId = authUtil.getCurrentUserId();
        return projectMemberRepository
                .findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(projectPermission))
                .orElse(false);
    }

}
