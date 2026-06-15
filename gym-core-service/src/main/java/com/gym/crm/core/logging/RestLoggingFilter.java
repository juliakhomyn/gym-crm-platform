package com.gym.crm.core.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RestLoggingFilter extends OncePerRequestFilter {
    private static final String PASSWORD_REGEX = "(\"password\"\\s*:\\s*\")([^\"]+)(\")";
    private static final String OLD_PASSWORD_REGEX = "(\"oldPassword\"\\s*:\\s*\")([^\"]+)(\")";
    private static final String NEW_PASSWORD_REGEX = "(\"newPassword\"\\s*:\\s*\")([^\"]+)(\")";
    private static final String REPLACEMENT = "$1***$3";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        long startTime = System.currentTimeMillis();
        log.info("Incoming request: method={} uri={} query={}", requestWrapper.getMethod(), requestWrapper.getRequestURI(), requestWrapper.getQueryString());

        try {
            chain.doFilter(requestWrapper, response);
        } finally {
            String requestBody = maskSensitiveData(getRequestBody(requestWrapper));
            long duration = System.currentTimeMillis() - startTime;

            log.info("Outgoing response: method={} uri={} status={} duration={}ms requestBody={}",
                    requestWrapper.getMethod(), requestWrapper.getRequestURI(), response.getStatus(), duration, requestBody);
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        return new String(content, StandardCharsets.UTF_8);
    }

    private String maskSensitiveData(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        return body
                .replaceAll(PASSWORD_REGEX, REPLACEMENT)
                .replaceAll(OLD_PASSWORD_REGEX, REPLACEMENT)
                .replaceAll(NEW_PASSWORD_REGEX, REPLACEMENT);
    }
}
