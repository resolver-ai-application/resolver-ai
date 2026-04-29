package com.projects.resolver.service;

import com.projects.resolver.dto.Project.ProjectRequest;
import com.projects.resolver.dto.Project.ProjectResponse;
import com.projects.resolver.dto.Project.ProjectSummaryResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects(Long userId);

    ProjectResponse getUserProjectById(Long projectId, Long userId);

    ProjectResponse createProject(ProjectRequest projectRequest, Long userId);

    ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest, Long userId);

    void softProject(Long projectId, Long userId);
}
