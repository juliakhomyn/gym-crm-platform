package com.gym.crm.workload.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.workload.openapi.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.gym.crm.workload.exception.ApiError.AUTHENTICATION_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String message = AUTHENTICATION_ERROR.getMessage() + getErrorMessage(exception);
        log.warn(message);
        ErrorResponse errorResponse = new ErrorResponse(AUTHENTICATION_ERROR.getCode(), message);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private String getErrorMessage(AuthenticationException exception) {
        return switch (exception) {
            case DisabledException ignored -> "User is disabled";
            case LockedException ignored -> "User account is locked";
            case InsufficientAuthenticationException ignored -> "Invalid token";
            default -> "Unexpected error";
        };
    }
}
