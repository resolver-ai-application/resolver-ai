package com.projects.resolver.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.projects.resolver.enums.ProjectPermission.*;

@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    EDITOR(VIEW, EDIT, VIEW_MEMBERS),
    VIEWER(VIEW, VIEW_MEMBERS),
    OWNER(VIEW, EDIT, VIEW_MEMBERS, MANAGE_MEMBERS, DELETE);

    ProjectRole(ProjectPermission... projectPermissions){
        this.permissions = Set.of(projectPermissions);
    }

    private final Set<ProjectPermission> permissions;
}
