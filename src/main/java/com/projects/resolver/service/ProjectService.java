package com.projects.resolver.service;

import com.projects.resolver.dto.Project.ProjectRequest;
import com.projects.resolver.dto.Project.ProjectResponse;
import com.projects.resolver.dto.Project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectResponse getUserProjectById(Long projectId);

    ProjectResponse createProject(ProjectRequest projectRequest);

    ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest);

    void softProject(Long projectId);
}
