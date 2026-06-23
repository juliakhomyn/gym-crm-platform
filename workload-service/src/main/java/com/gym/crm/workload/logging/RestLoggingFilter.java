package com.gym.crm.workload.logging;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var wrapper = new ContentCachingRequestWrapper(request);

        long startTime = System.currentTimeMillis();
        log.info("Incoming request: method={} uri={} query={}", wrapper.getMethod(), wrapper.getRequestURI(), wrapper.getQueryString());

        try {
            chain.doFilter(wrapper, response);
        } finally {
            String requestBody = getRequestBody(wrapper);
            long duration = System.currentTimeMillis() - startTime;

            log.info("Outgoing response: method={} uri={} status={} duration={}ms requestBody={}",
                    wrapper.getMethod(), wrapper.getRequestURI(), response.getStatus(), duration, requestBody);
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }

        return new String(content, StandardCharsets.UTF_8);
    }
}
