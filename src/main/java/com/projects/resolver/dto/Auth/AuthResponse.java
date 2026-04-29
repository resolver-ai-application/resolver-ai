package com.projects.resolver.dto.Auth;

public record AuthResponse(
        String token,
        UserProfileResponse userProfileResponse)
{

}
