package com.projects.resolver.advices;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 *  Represents a standardized API error response returned by the application.
 * @param status
 * @param message
 * @param timestamp
 * @param errors
 */
public record ApiError(
        HttpStatus status,
        String message,
        Instant timestamp,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<ApiFieldError> errors
) {
    public ApiError(HttpStatus status, String message){
        this(status, message, Instant.now(), null);
    }
    public ApiError(HttpStatus status, String message, List<ApiFieldError> errors){
        this(status, message, Instant.now(), errors);
    }
}

/**
 * Represents a field-specific validation error in an API request.
 * @param field
 * @param message
 */
record ApiFieldError(String field, String message){}
