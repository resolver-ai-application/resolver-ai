package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Project.ProjectRequest;
import com.projects.resolver.dto.Project.ProjectResponse;
import com.projects.resolver.dto.Project.ProjectSummaryResponse;
import com.projects.resolver.entity.Project;
import com.projects.resolver.entity.User;
import com.projects.resolver.exceptions.BadRequestException;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import com.projects.resolver.mapper.ProjectMapper;
import com.projects.resolver.repositories.ProjectRepository;
import com.projects.resolver.repositories.UserRepository;
import com.projects.resolver.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
        List<Project> projectList = projectRepository.findAllAccesibleByUser(userId);
        return projectMapper.toProjectSummaryResponses(projectList);
    }

    @Override
    public ProjectResponse getUserProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findAccessibleProjectById(userId, projectId).orElseThrow(
                ()->new ResourceNotFoundException("Project owner not exist",projectId.toString())
        );
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not present",userId.toString()));
        Project project = Project.builder()
                .name(projectRequest.name())
                .owner(owner)
                .isPublic(false)
                .build();
        project=projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest, Long userId) {
        Project project = this.findAccessibleProjectById(userId,projectId);
        project.setName(projectRequest.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softProject(Long projectId, Long userId) {
        Project project = this.findAccessibleProjectById(userId,projectId);
        if(!project.getOwner().getId().equals(userId))
            throw new BadRequestException("You are not allowed to delete");
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    private Project findAccessibleProjectById(Long userId, Long projectId){
        return projectRepository.findAccessibleProjectById(userId,projectId).orElseThrow(
                ()->new ResourceNotFoundException("Project not exist",projectId.toString())
        );
    }
}
