package com.projects.resolver.service;

import com.projects.resolver.dto.Member.InviteMemberRequest;
import com.projects.resolver.dto.Member.MemberResponse;
import com.projects.resolver.dto.Member.UpdateMemberRoleRequest;
import com.projects.resolver.entity.ProjectMember;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long userId, Long projectId);

    MemberResponse inviteMember(Long projectId, Long userId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long userId, Long memberId, UpdateMemberRoleRequest request);

    void deleteProjectMember(Long projectId, Long userId, Long memberId);
}
