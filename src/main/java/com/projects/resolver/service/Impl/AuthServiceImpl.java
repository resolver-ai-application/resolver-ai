package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Auth.AuthResponse;
import com.projects.resolver.dto.Auth.LoginRequest;
import com.projects.resolver.dto.Auth.SignUpRequest;
import com.projects.resolver.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {


    @Override
    public AuthResponse signup(SignUpRequest signUpRequest) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        return null;
    }
}
