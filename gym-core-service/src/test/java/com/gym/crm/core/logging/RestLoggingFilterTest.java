package com.gym.crm.core.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RestLoggingFilterTest {

    private final RestLoggingFilter filter = new RestLoggingFilter();

    @Test
    void doFilterInternal_shouldContinueFilterChain() throws ServletException, IOException {
        MockHttpServletRequest request = buildRequest("GET", "/api/v1/trainees/Simone.Radcliffe", null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(any(ContentCachingRequestWrapper.class), eq(response));
    }

    @Test
    void doFilterInternal_whenEmptyBody_shouldNotThrow() {
        MockHttpServletRequest request = buildRequest("GET", "/api/v1/trainees/Simone.Radcliffe", null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        assertThatCode(() -> filter.doFilterInternal(request, response, chain)).doesNotThrowAnyException();
    }

    @Test
    void doFilterInternal_whenRequestHasJsonBody_shouldNotThrow() {
        MockHttpServletRequest request = buildRequest("POST", "/api/v1/auth/login",
                "{\"username\":\"Simone.Radcliffe\",\"password\":\"password123\"}");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        assertDoesNotThrow(() -> filter.doFilterInternal(request, response, chain));
    }

    @Test
    void getRequestBody_whenContentIsEmpty_shouldReturnEmptyString() throws Exception {
        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(new MockHttpServletRequest());

        String actual = invokeGetRequestBody(request);

        assertThat(actual).isEmpty();
    }

    @Test
    void maskSensitiveData_shouldMaskPasswordField() throws Exception {
        String body = "{\"username\":\"Simone.Radcliffe\",\"password\":\"secret123\"}";

        String actual = invokeMaskSensitiveData(body);

        assertThat(actual).isEqualTo("{\"username\":\"Simone.Radcliffe\",\"password\":\"***\"}");
    }

    @Test
    void maskSensitiveData_shouldMaskOldAndNewPassword() throws Exception {
        String body = """
                {
                  "username": "Simone.Radcliffe",
                  "oldPassword": "oldPass",
                  "newPassword": "newPass"
                }
                """;
        String expected = """
                {
                  "username": "Simone.Radcliffe",
                  "oldPassword": "***",
                  "newPassword": "***"
                }
                """;

        String actual = invokeMaskSensitiveData(body);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void maskSensitiveData_whenNull_shouldReturnEmptyString() throws Exception {
        assertThat(invokeMaskSensitiveData(null)).isEmpty();
    }

    @Test
    void maskSensitiveData_whenBlank_shouldReturnEmptyString() throws Exception {
        assertThat(invokeMaskSensitiveData("   ")).isEmpty();
    }

    @Test
    void maskSensitiveData_shouldNotMaskNonPasswordFields() throws Exception {
        String body = "{\"username\":\"Simone.Radcliffe\",\"firstName\":\"Simone\"}";

        String actual = invokeMaskSensitiveData(body);

        assertThat(actual).isEqualTo(body);
    }

    private MockHttpServletRequest buildRequest(String method, String uri, String body) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(method);
        request.setRequestURI(uri);

        if (body != null) {
            request.setContent(body.getBytes(StandardCharsets.UTF_8));
            request.setContentType("application/json");
        }

        return request;
    }

    private String invokeGetRequestBody(ContentCachingRequestWrapper request) throws Exception {
        Method method = RestLoggingFilter.class.getDeclaredMethod("getRequestBody", ContentCachingRequestWrapper.class);
        method.setAccessible(true);

        return (String) method.invoke(filter, request);
    }

    private String invokeMaskSensitiveData(String body) throws Exception {
        Method method = RestLoggingFilter.class.getDeclaredMethod("maskSensitiveData", String.class);
        method.setAccessible(true);

        return (String) method.invoke(filter, body);
    }
}
