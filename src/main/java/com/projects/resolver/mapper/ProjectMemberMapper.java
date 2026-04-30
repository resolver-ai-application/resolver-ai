package com.projects.resolver.mapper;

import com.projects.resolver.dto.Member.MemberResponse;
import com.projects.resolver.entity.ProjectMember;
import com.projects.resolver.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "projectRole", constant= "OWNER")
    MemberResponse toMemberResponse(User owner);

    List<MemberResponse> toMemberResponseList(List<ProjectMember> projectMemberList);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "name", source = "user.name")
    MemberResponse toMemberResponse(ProjectMember projectMemberList);
}
