package com.gym.crm.workload.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRACE_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String traceId = req.getHeader(TRACE_HEADER);

        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID, traceId);
        res.setHeader(TRACE_HEADER, traceId);

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }
}
