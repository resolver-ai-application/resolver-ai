package com.projects.resolver.mapper;

import com.projects.resolver.dto.Project.ProjectResponse;
import com.projects.resolver.dto.Project.ProjectSummaryResponse;
import com.projects.resolver.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

//    @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd")
    List<ProjectSummaryResponse> toProjectSummaryResponses(List<Project> projectList);

}
