package com.gym.crm.core.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtPropagationInterceptorTest {

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    @InjectMocks
    private JwtPropagationInterceptor interceptor;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void intercept_shouldSetAuthorizationHeader_whenHeaderPresent() throws IOException {
        String token = "Bearer test.jwt.token";
        HttpHeaders headers = new HttpHeaders();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(httpRequest.getHeaders()).thenReturn(headers);
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo(token);
        verify(execution).execute(httpRequest, new byte[0]);
    }

    @Test
    void intercept_shouldNotSetAuthorizationHeader_whenHeaderAbsent() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
        verify(execution).execute(httpRequest, new byte[0]);
    }

    @Test
    void intercept_shouldNotSetAuthorizationHeader_whenNoRequestAttributes() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
        verify(execution).execute(httpRequest, new byte[0]);
    }
}
