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
import com.projects.resolver.service.ProjectMemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.management.RuntimeErrorException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;

    @Override
    public List<MemberResponse> getProjectMembers(Long userId, Long projectId) {
        Project project = findAccessibleProjectById(userId, projectId);
        List<MemberResponse> memberResponseList = new ArrayList<>();
        memberResponseList.add(projectMemberMapper.toMemberResponse(project.getOwner()));
        List<ProjectMember> memberList = projectMemberRepository.findByIdProjectId(projectId);
        memberResponseList.addAll(projectMemberMapper.toMemberResponseList(memberList));
        return memberResponseList;
    }

    @Override
    public MemberResponse inviteMember(Long projectId, Long userId, InviteMemberRequest request) {
       Project project = this.findAccessibleProjectById(userId,projectId);
       if(!project.getOwner().getId().equals(userId))
           throw new BadRequestException("Not Allowed");
       User invitee = userRepository.findByEmail(request.email()).orElseThrow();
       if(invitee.getId().equals(userId))
           throw new BadRequestException("Cannot Invite Yourself");
       ProjectMemberId projectMemberId = new ProjectMemberId(projectId,userId);
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
    public MemberResponse updateMemberRole(Long projectId, Long userId, Long memberId, UpdateMemberRoleRequest request) {
        Project project = this.findAccessibleProjectById(userId,projectId);
        if(!project.getOwner().getId().equals(userId))
            throw new BadRequestException("Not Allowed");
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow(
                () -> new ResourceNotFoundException("Project member dont exist",projectMemberId.toString())
        );
        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toMemberResponse(projectMember);
    }

    @Override
    public void deleteProjectMember(Long projectId, Long userId, Long memberId) {
        Project project = this.findAccessibleProjectById(userId,projectId);
        if(!project.getOwner().getId().equals(userId))
            throw new BadRequestException("Not Allowed");
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
