package com.gym.crm.core.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Component
@Slf4j
public class ClientRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest servletRequest = attributes.getRequest();

            propagateAuthHeader(request, servletRequest);
            propagateTraceId(request, servletRequest);
        } else {
            log.warn("No ServletRequestAttributes found. Cannot propagate headers.");
        }

        return execution.execute(request, body);
    }

    private void propagateAuthHeader(HttpRequest request, HttpServletRequest servletRequest) {
        String authHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, authHeader);

            log.info("Propagating Authorization header to downstream request");
        } else {
            log.info("No Authorization header found in incoming request to propagate.");
        }
    }

    private void propagateTraceId(HttpRequest request, HttpServletRequest servletRequest) {
        String traceId = servletRequest.getHeader(TRACE_HEADER);
        if (traceId == null) {
            traceId = (String) servletRequest.getAttribute(TRANSACTION_ID);
        }

        if (traceId == null) {
            traceId = MDC.get(TRANSACTION_ID);
        }

        if (traceId != null) {
            request.getHeaders().set(TRACE_HEADER, traceId);

            log.info("Propagating X-Trace-Id header to downstream request: {}", traceId);
        } else {
            log.info("No X-Trace-Id header found in incoming request to propagate.");
        }
    }
}