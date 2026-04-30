package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Auth.UserProfileResponse;
import com.projects.resolver.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }
}
