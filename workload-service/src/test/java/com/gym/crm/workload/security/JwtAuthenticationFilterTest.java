package com.gym.crm.workload.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private static final String VALID_BEARER_TOKEN = "Bearer validToken";
    private static final String VALID_TOKEN = "validToken";
    
    @Mock
    private JwtService jwtService;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void doFilterInternal_shouldContinueChain_whenNoAuthorizationHeader() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldContinueChain_whenHeaderNotBearer() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic abcdef");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldContinueChain_whenTokenInvalid() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalidtoken");
        when(jwtService.isTokenValid("invalidtoken")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldContinueChain_whenUsernameNull() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, VALID_BEARER_TOKEN);
        when(jwtService.isTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldSetAuthentication_whenTokenValidAndUsernamePresent() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, VALID_BEARER_TOKEN);
        when(jwtService.isTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn("testuser");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo("testuser");
        assertThat(auth.getAuthorities()).isEmpty();
    }
}
