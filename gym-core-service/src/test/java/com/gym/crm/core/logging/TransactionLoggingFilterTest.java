package com.gym.crm.core.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TransactionLoggingFilterTest {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRACE_HEADER = "X-Trace-Id";

    private final TransactionLoggingFilter filter = new TransactionLoggingFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldGenerateNewTraceIdIfHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        String txId = response.getHeader(TRACE_HEADER);
        assertThat(txId)
                .isNotNull()
                .isNotBlank();
        assertThatCode(() -> UUID.fromString(txId)).doesNotThrowAnyException();
        assertThat(MDC.get(TRANSACTION_ID)).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldUseExistingTraceIdFromHeader() throws Exception {
        String existingId = "test-trace-id-123";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(TRACE_HEADER, existingId);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        String txId = response.getHeader(TRACE_HEADER);
        assertThat(txId).isEqualTo(existingId);
        assertThat(MDC.get(TRANSACTION_ID)).isNull();
    }

    @Test
    void shouldSetAndRemoveMDC() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            String txId = MDC.get(TRANSACTION_ID);
            assertThat(txId)
                    .isNotNull()
                    .isNotBlank();
        };

        filter.doFilterInternal(request, response, chain);

        assertThat(MDC.get(TRANSACTION_ID)).isNull();
    }

    @Test
    void shouldRemoveMDCEvenOnException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            throw new ServletException("Test exception");
        };

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, chain)).isInstanceOf(ServletException.class);
        assertThat(MDC.get(TRANSACTION_ID)).isNull();
    }

    @Test
    void shouldGenerateUniqueTransactionIdPerRequest() throws Exception {
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(new MockHttpServletRequest(), response1, chain);
        filter.doFilterInternal(new MockHttpServletRequest(), response2, chain);

        String id1 = response1.getHeader(TRACE_HEADER);
        String id2 = response2.getHeader(TRACE_HEADER);

        assertThat(id1).isNotEqualTo(id2);
    }
}
