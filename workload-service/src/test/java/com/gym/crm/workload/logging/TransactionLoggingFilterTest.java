package com.gym.crm.workload.logging;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TransactionLoggingFilterTest {
    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRACE_HEADER = "X-Trace-Id";

    private TransactionLoggingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new TransactionLoggingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldGenerateNewTraceIdIfHeaderMissing() throws Exception {
        filter.doFilterInternal(request, response, chain);

        String txId = response.getHeader(TRACE_HEADER);
        assertThat(txId).isNotNull().isNotBlank();
        assertThatCode(() -> UUID.fromString(txId)).doesNotThrowAnyException();
        assertThat(MDC.get(TRANSACTION_ID)).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldUseExistingTraceIdFromHeader() throws Exception {
        String existingId = "test-trace-id-123";
        request.addHeader(TRACE_HEADER, existingId);

        filter.doFilterInternal(request, response, chain);

        String txId = response.getHeader(TRACE_HEADER);
        assertThat(txId).isEqualTo(existingId);
        assertThat(MDC.get(TRANSACTION_ID)).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldSetAndRemoveMDC() throws Exception {
        doAnswer(invocation -> {
            String txId = MDC.get(TRANSACTION_ID);
            assertThat(txId).isNotNull().isNotBlank();

            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        assertThat(MDC.get(TRANSACTION_ID)).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldGenerateUniqueTransactionIdPerRequest() throws Exception {
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        FilterChain chain1 = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);
        filter.doFilterInternal(request1, response1, chain1);

        String id1 = response.getHeader(TRACE_HEADER);
        String id2 = response1.getHeader(TRACE_HEADER);
        assertThat(id1).isNotNull();
        assertThat(id2).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
    }
}
