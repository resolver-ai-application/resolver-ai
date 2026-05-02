package com.projects.resolver.mapper;

import com.projects.resolver.dto.Auth.SignUpRequest;
import com.projects.resolver.dto.Auth.UserProfileResponse;
import com.projects.resolver.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignUpRequest signUpRequest);

    UserProfileResponse toUserProfileResponse(User user);

}
