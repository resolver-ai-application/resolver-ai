package com.projects.resolver.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthFilter extends OncePerRequestFilter {

    AuthUtil authUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
             HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("incoming request: {}", request.getRequestURI());

        final String requestHeaderToken = request.getHeader("Authorization");
        if(Objects.isNull(requestHeaderToken) || !requestHeaderToken.startsWith("Bearer")){
            filterChain.doFilter(request,response);
        }
        /**
         * Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYUBnbWFpbC5jb20iLCJ1c2VySWQiOiIzIiwia
         * WF0IjoxNzc3Njk5NzQwLCJleHAiOjE3Nzc3MDAzNDB9.C_haeqYogwWH5wU8OBInCm-sI6mrpBegtHSoW1
         * -9umKlGOWeRgHdooxHLF4regFM-BMBaMZ39fwrcJW6TWPpAg"
         */
        String token = requestHeaderToken.split("Bearer ")[1];
        JwtUserPrincipal user = authUtil.verifyAccessToken(token);
        if(Objects.nonNull(user) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())){
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user,null, user.authorityList()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request,response);
    }
}
