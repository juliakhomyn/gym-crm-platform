package com.gym.crm.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ErrorResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static com.gym.crm.core.exception.ApiError.AUTHENTICATION_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomAuthenticationEntryPoint entryPoint;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletOutputStream outputStream;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        outputStream = mock(ServletOutputStream.class);
    }

    @Test
    void commence_shouldHandleInsufficientAuthenticationException() throws IOException {
        AuthenticationException authException = new InsufficientAuthenticationException("invalid");

        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());
        ErrorResponse actualError = errorCaptor.getValue();
        assertThat(actualError).isNotNull();
        assertThat(actualError.getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(actualError.getErrorMessage()).contains("Invalid token");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
    }

    @Test
    void commence_shouldHandleDisabledException() throws IOException {
        AuthenticationException authException = new DisabledException("disabled");

        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());
        ErrorResponse actualError = errorCaptor.getValue();
        assertThat(actualError).isNotNull();
        assertThat(actualError.getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(actualError.getErrorMessage()).contains("User is disabled");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
    }

    @Test
    void commence_shouldHandleLockedException() throws IOException {
        AuthenticationException authException = new LockedException("locked");

        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());
        ErrorResponse actualError = errorCaptor.getValue();
        assertThat(actualError).isNotNull();
        assertThat(actualError.getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(actualError.getErrorMessage()).contains("User account is locked");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
    }

    @Test
    void commence_shouldHandleOtherAuthenticationException() throws IOException {
        AuthenticationException authException = new AuthenticationException("unexpected") {};

        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());
        ErrorResponse actualError = errorCaptor.getValue();
        assertThat(actualError).isNotNull();
        assertThat(actualError.getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(actualError.getErrorMessage()).contains("Unexpected error");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
    }
}
