package com.projects.resolver.advices;

import com.projects.resolver.exceptions.BadRequestException;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getResourceName()
            + " with id " + ex.getResourceId());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInputValidationException(MethodArgumentNotValidException ex){
        List<ApiFieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiFieldError(error.getField(), error.getDefaultMessage())).toList();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Input Validation failed",errors);
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex){
        ApiError errors = new ApiError(HttpStatus.NOT_FOUND,"Username not found with username "+ex.getMessage());
        log.error(errors.toString(), ex);
        return ResponseEntity.status(errors.status()).body(errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex){
        ApiError errors = new ApiError(HttpStatus.UNAUTHORIZED,"Authentication failed "+ex.getMessage());
        log.error(errors.toString(), ex);
        return ResponseEntity.status(errors.status()).body(errors);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex){
        ApiError errors = new ApiError(HttpStatus.UNAUTHORIZED,"Invalid JWT token "+ex.getMessage());
        log.error(errors.toString(), ex);
        return ResponseEntity.status(errors.status()).body(errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex){
        ApiError errors = new ApiError(HttpStatus.FORBIDDEN,"Access denied "+ex.getMessage());
        log.error(errors.toString(), ex);
        return ResponseEntity.status(errors.status()).body(errors);
    }

}
