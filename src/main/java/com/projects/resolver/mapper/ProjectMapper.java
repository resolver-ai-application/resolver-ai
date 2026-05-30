package com.projects.resolver.mapper;

import com.projects.resolver.dto.Project.ProjectResponse;
import com.projects.resolver.dto.Project.ProjectSummaryResponse;
import com.projects.resolver.entity.Project;
import com.projects.resolver.enums.ProjectRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

//    @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd")
    List<ProjectSummaryResponse> toProjectSummaryResponses(List<Project> projectList);

}
