package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Member.InviteMemberRequest;
import com.projects.resolver.dto.Member.MemberResponse;
import com.projects.resolver.dto.Member.UpdateMemberRoleRequest;
import com.projects.resolver.entity.Project;
import com.projects.resolver.entity.ProjectMember;
import com.projects.resolver.entity.ProjectMemberId;
import com.projects.resolver.entity.User;
import com.projects.resolver.exceptions.BadRequestException;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import com.projects.resolver.mapper.ProjectMemberMapper;
import com.projects.resolver.repositories.ProjectMemberRepository;
import com.projects.resolver.repositories.ProjectRepository;
import com.projects.resolver.repositories.UserRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.ProjectMemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;
    AuthUtil authUtil;

    @Override
    public List<MemberResponse> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toMemberResponse)
                .toList();
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();
       Project project = this.findAccessibleProjectById(userId,projectId);
       User invitee = userRepository.findByUsername(request.username()).orElseThrow();
       if(invitee.getId().equals(userId))
           throw new BadRequestException("Cannot Invite Yourself");
       ProjectMemberId projectMemberId = new ProjectMemberId(projectId,invitee.getId());
       if(projectMemberRepository.existsById(projectMemberId))
           throw new BadRequestException("Cannot Invite Again");
       ProjectMember member = ProjectMember.builder()
               .id(projectMemberId)
               .project(project)
               .user(invitee)
               .projectRole(request.role())
               .invitedAt(Instant.now())
               .build();
       projectMemberRepository.save(member);
       return projectMemberMapper.toMemberResponse(member);
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request) {
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow(
                () -> new ResourceNotFoundException("Project member dont exist",projectMemberId.toString())
        );
        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toMemberResponse(projectMember);
    }

    @Override
    public void deleteProjectMember(Long projectId, Long memberId) {
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);
        if(!projectMemberRepository.existsById(projectMemberId))
            throw new BadRequestException("Cannot Remove Invite of uninvited member");
        projectMemberRepository.deleteById(projectMemberId);
    }

    private Project findAccessibleProjectById(Long userId, Long projectId){
        return projectRepository.findAccessibleProjectById(userId,projectId).orElseThrow(
                ()->new ResourceNotFoundException("Project not exist",projectId.toString())
        );
    }
}
