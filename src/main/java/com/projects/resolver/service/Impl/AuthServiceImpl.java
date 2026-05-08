package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Auth.AuthResponse;
import com.projects.resolver.dto.Auth.LoginRequest;
import com.projects.resolver.dto.Auth.SignUpRequest;
import com.projects.resolver.entity.User;
import com.projects.resolver.exceptions.BadRequestException;
import com.projects.resolver.mapper.UserMapper;
import com.projects.resolver.repositories.UserRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;


    /**
     * Sign up User
     * @param signUpRequest
     * @return
     */
    @Override
    public AuthResponse signup(SignUpRequest signUpRequest) {
        userRepository.findByUsername(signUpRequest.username()).ifPresent(user -> {
            throw new BadRequestException("User already exist with username : " + signUpRequest.username());
        });
        User user = userMapper.toEntity(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user = userRepository.save(user);
        return new AuthResponse(authUtil.generateAccessToken(user),userMapper.toUserProfileResponse(user));
    }

    /**
     * User Login with username and password
     * @param loginRequest
     * @return
     */
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(),loginRequest.password())
        );
        User user = (User) authentication.getPrincipal();
        user = userRepository.save(user);
        return new AuthResponse(authUtil.generateAccessToken(user),userMapper.toUserProfileResponse(user));
    }
}
