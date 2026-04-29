package com.projects.resolver.controller;

import com.projects.resolver.dto.Member.InviteMemberRequest;
import com.projects.resolver.dto.Member.MemberResponse;
import com.projects.resolver.dto.Member.UpdateMemberRoleRequest;
import com.projects.resolver.entity.ProjectMember;
import com.projects.resolver.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<ProjectMember>> getProjectMembers(@PathVariable Long projectId){
        Long userId = 1L;
        return ResponseEntity.ok(projectMemberService.getProjectMembers(userId,projectId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(@PathVariable Long projectId, @RequestBody InviteMemberRequest request){
        Long userId =1L;
        return ResponseEntity.ok(projectMemberService.inviteMember(projectId,userId,request));
    }

    @PatchMapping("/{memberId")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long projectId, @PathVariable Long memberId, @RequestBody UpdateMemberRoleRequest request){
        Long userId =1L;
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId,userId,memberId,request));
    }//todo why UpdateMemberRoleRequest is not used here?

    @DeleteMapping("/{memberId")
    public ResponseEntity<MemberResponse> deleteProjectMember(@PathVariable Long projectId, @PathVariable Long memberId){
        Long userId =1L;
        return ResponseEntity.ok(projectMemberService.deleteProjectMember(projectId,userId,memberId));
    }
}
