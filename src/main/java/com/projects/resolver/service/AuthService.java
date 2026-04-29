package com.projects.resolver.service;

import com.projects.resolver.dto.Auth.AuthResponse;
import com.projects.resolver.dto.Auth.LoginRequest;
import com.projects.resolver.dto.Auth.SignUpRequest;
import org.jspecify.annotations.Nullable;

public interface AuthService {

    AuthResponse signup(SignUpRequest signUpRequest);
    AuthResponse login(LoginRequest loginRequest);
}
