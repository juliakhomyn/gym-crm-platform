package com.gym.crm.core.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
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
class ClientRequestInterceptorTest {
    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final String TRANSACTION_ID = "transactionId";

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    @InjectMocks
    private ClientRequestInterceptor interceptor;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void intercept_shouldSetAuthorizationAndTraceIdHeaders_whenHeadersPresent() throws IOException {
        String token = "Bearer test.jwt.token";
        String traceId = "trace-123";
        HttpHeaders headers = new HttpHeaders();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(servletRequest.getHeader(TRACE_HEADER)).thenReturn(traceId);
        when(httpRequest.getHeaders()).thenReturn(headers);
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo(token);
        assertThat(headers.getFirst(TRACE_HEADER)).isEqualTo(traceId);
        verify(execution).execute(httpRequest, new byte[0]);
    }

    @Test
    void intercept_shouldSetOnlyAuthorizationHeader_whenOnlyAuthorizationPresent() throws IOException {
        String token = "Bearer test.jwt.token";
        HttpHeaders headers = new HttpHeaders();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(servletRequest.getHeader(TRACE_HEADER)).thenReturn(null);
        when(httpRequest.getHeaders()).thenReturn(headers);
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo(token);
        assertThat(headers.containsKey(TRACE_HEADER)).isFalse();
        verify(execution).execute(httpRequest, new byte[0]);
    }

    @Test
    void intercept_shouldSetOnlyTraceIdHeader_whenOnlyTraceIdPresent() throws IOException {
        String traceId = "trace-456";
        HttpHeaders headers = new HttpHeaders();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(servletRequest.getHeader(TRACE_HEADER)).thenReturn(traceId);
        when(httpRequest.getHeaders()).thenReturn(headers);
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
        assertThat(headers.getFirst(TRACE_HEADER)).isEqualTo(traceId);
        verify(execution).execute(httpRequest, new byte[0]);
    }

    @Test
    void intercept_shouldSetTraceIdFromRequestAttribute_whenHeaderAbsent() throws IOException {
        String traceId = "trace-attr-789";
        HttpHeaders headers = new HttpHeaders();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(servletRequest.getHeader(TRACE_HEADER)).thenReturn(null);
        when(servletRequest.getAttribute(TRANSACTION_ID)).thenReturn(traceId);
        when(httpRequest.getHeaders()).thenReturn(headers);
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.getFirst(TRACE_HEADER)).isEqualTo(traceId);
        verify(execution).execute(httpRequest, new byte[0]);
    }

    @Test
    void intercept_shouldSetTraceIdFromMDC_whenHeaderAndAttributeAbsent() throws IOException {
        String traceId = "trace-mdc-999";
        HttpHeaders headers = new HttpHeaders();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));
        MDC.put(TRANSACTION_ID, traceId);

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(servletRequest.getHeader(TRACE_HEADER)).thenReturn(null);
        when(servletRequest.getAttribute(TRANSACTION_ID)).thenReturn(null);
        when(httpRequest.getHeaders()).thenReturn(headers);
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.getFirst(TRACE_HEADER)).isEqualTo(traceId);
        verify(execution).execute(httpRequest, new byte[0]);
    }

    @Test
    void intercept_shouldNotSetAnyHeaders_whenNoHeadersPresent() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(servletRequest.getHeader(TRACE_HEADER)).thenReturn(null);
        when(servletRequest.getAttribute(TRANSACTION_ID)).thenReturn(null);
        when(execution.execute(any(), any())).thenReturn(response);

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
        assertThat(headers.containsKey(TRACE_HEADER)).isFalse();
        verify(execution).execute(httpRequest, new byte[0]);
    }

    @Test
    void intercept_shouldNotSetAnyHeaders_whenNoRequestAttributes() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        when(execution.execute(any(), any())).thenReturn(response);

        RequestContextHolder.resetRequestAttributes();

        interceptor.intercept(httpRequest, new byte[0], execution);

        assertThat(headers.containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
        assertThat(headers.containsKey(TRACE_HEADER)).isFalse();
        verify(execution).execute(httpRequest, new byte[0]);
    }
}
