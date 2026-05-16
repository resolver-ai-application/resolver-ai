package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Project.ProjectRequest;
import com.projects.resolver.dto.Project.ProjectResponse;
import com.projects.resolver.dto.Project.ProjectSummaryResponse;
import com.projects.resolver.entity.Project;
import com.projects.resolver.entity.ProjectMember;
import com.projects.resolver.entity.ProjectMemberId;
import com.projects.resolver.entity.User;
import com.projects.resolver.enums.ProjectRole;
import com.projects.resolver.exceptions.BadRequestException;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import com.projects.resolver.mapper.ProjectMapper;
import com.projects.resolver.repositories.ProjectMemberRepository;
import com.projects.resolver.repositories.ProjectRepository;
import com.projects.resolver.repositories.UserRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.ProjectService;
import com.projects.resolver.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
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
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;
    SubscriptionService subscriptionService;

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
        List<Project> projectList = projectRepository.findAllAccesibleByUser(userId);
        return projectMapper.toProjectSummaryResponses(projectList);
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = projectRepository.findAccessibleProjectById(userId, projectId).orElseThrow(
                ()->new ResourceNotFoundException("Project owner not exist",projectId.toString())
        );
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        Long userId = authUtil.getCurrentUserId();
        if(!subscriptionService.canCreateNewProject()){
            throw new BadRequestException("User cannot create a new Project with current Plan, Upgrade Plan Now");
        }
        User owner = userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not present",userId.toString()));
        Project project = Project.builder()
                .name(projectRequest.name())
                .isPublic(false)
                .build();
        project=projectRepository.save(project);

        //After project got created, Add Owner as ProjectMember
        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), userId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest) {
        Long userId = authUtil.getCurrentUserId();
        Project project = this.findAccessibleProjectById(userId,projectId);
        project.setName(projectRequest.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softProject(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = this.findAccessibleProjectById(userId,projectId);
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    private Project findAccessibleProjectById(Long userId, Long projectId){
        return projectRepository.findAccessibleProjectById(userId,projectId).orElseThrow(
                ()->new ResourceNotFoundException("Project not exist",projectId.toString())
        );
    }
}
