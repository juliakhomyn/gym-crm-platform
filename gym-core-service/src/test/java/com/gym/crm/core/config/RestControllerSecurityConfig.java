package com.gym.crm.core.config;

import com.gym.crm.core.security.CustomAuthenticationEntryPoint;
import com.gym.crm.core.security.CustomUserDetailsService;
import com.gym.crm.core.security.JwtAuthenticationFilter;
import com.gym.crm.core.security.JwtService;
import com.gym.crm.core.security.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@TestConfiguration
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, JwtAuthenticationFilter.class, JwtService.class})
public class RestControllerSecurityConfig {

    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
                                                           CustomUserDetailsService userDetailsService,
                                                           TokenBlacklistService tokenBlacklistService) {
        return new FakeJwtAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
    }

    private static class FakeJwtAuthenticationFilter extends JwtAuthenticationFilter {
        public FakeJwtAuthenticationFilter(JwtService jwtService,
                                           CustomUserDetailsService userDetailsService,
                                           TokenBlacklistService tokenBlacklistService) {
            super(jwtService, userDetailsService, tokenBlacklistService);
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            filterChain.doFilter(request, response);
        }
    }
}
