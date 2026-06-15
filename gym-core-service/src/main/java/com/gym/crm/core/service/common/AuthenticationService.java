package com.gym.crm.core.service.common;

import com.gym.crm.core.facade.dto.common.AuthRequestDTO;
import com.gym.crm.core.facade.dto.common.AuthResponseDTO;
import com.gym.crm.core.exception.UserAuthenticationException;
import com.gym.crm.core.model.User;
import com.gym.crm.core.exception.BadCredentialsException;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.repository.UserRepository;
import com.gym.crm.core.security.BruteForceProtectionService;
import com.gym.crm.core.security.JwtService;
import com.gym.crm.core.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_HEADER_ERROR = "Missing or malformed Authorization header";

    private final UserRepository repository;
    private final UserProfileService service;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final BruteForceProtectionService bruteForceProtectionService;

    @Transactional(readOnly = true)
    public AuthResponseDTO authenticate(@Valid AuthRequestDTO dto) {
        String username = dto.getUsername();
        log.info("Authentication attempt for user: {}", username);

        bruteForceProtectionService.checkIfLocked(username);

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (!service.checkPassword(dto.getPassword(), user.getPassword())) {
            bruteForceProtectionService.loginFailed(username);
            throw new BadCredentialsException("Invalid credentials for user: " + username);
        }

        bruteForceProtectionService.loginSuccess(username);
        log.info("Authentication successful for user: {}", username);

        return AuthResponseDTO.builder()
                .username(user.getUsername())
                .token(jwtService.generateToken(user.getUsername()))
                .build();
    }

    public void logout(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new UserAuthenticationException(INVALID_HEADER_ERROR);
        }

        String token = header.substring(BEARER_PREFIX.length());
        String username = jwtService.extractUsername(token);
        log.info("Logging out user: username={}", username);

        tokenBlacklistService.blacklist(token);
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully: username{}", username);
    }
}
