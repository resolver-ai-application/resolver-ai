package com.projects.resolver.service;

import com.projects.resolver.dto.Auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
}
