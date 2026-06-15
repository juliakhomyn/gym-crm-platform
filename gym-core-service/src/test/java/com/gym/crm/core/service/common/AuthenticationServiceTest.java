package com.gym.crm.core.service.common;

import com.gym.crm.core.facade.dto.common.AuthRequestDTO;
import com.gym.crm.core.facade.dto.common.AuthResponseDTO;
import com.gym.crm.core.exception.BadCredentialsException;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.exception.UserAuthenticationException;
import com.gym.crm.core.model.User;
import com.gym.crm.core.repository.UserRepository;
import com.gym.crm.core.security.BruteForceProtectionService;
import com.gym.crm.core.security.JwtService;
import com.gym.crm.core.security.TokenBlacklistService;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String INVALID_PASSWORD = "invalidPassword";
    private static final String TOKEN = "token";

    private static final String INVALID_HEADER_ERROR = "Missing or malformed Authorization header";

    private final User user = TestDataProvider.buildTraineeUser();
    private final AuthRequestDTO requestDTO = TestDataProvider.buildAuthRequestDTO();
    private final AuthRequestDTO requestInvalidPassword = TestDataProvider.buildAuthRequestDTOWithInvalidPassword();

    @Mock
    private UserProfileService userProfileService;
    @Mock
    private UserRepository repository;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @Mock
    private BruteForceProtectionService bruteForceProtectionService;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void authenticate_shouldReturnResponse_whenCredentialsAreValid() {
        doNothing().when(bruteForceProtectionService).checkIfLocked(USERNAME);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(userProfileService.checkPassword(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(USERNAME)).thenReturn(TOKEN);

        AuthResponseDTO actual = service.authenticate(requestDTO);

        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getToken()).isEqualTo(TOKEN);
        verify(bruteForceProtectionService).loginSuccess(USERNAME);
        verify(repository).findByUsername(USERNAME);
        verify(userProfileService).checkPassword(PASSWORD, ENCODED_PASSWORD);
        verify(jwtService).generateToken(USERNAME);
    }

    @Test
    void authenticate_shouldThrowLockedException_whenUserIsLocked() {
        doThrow(new LockedException("locked for 5 minutes")).when(bruteForceProtectionService).checkIfLocked(USERNAME);

        assertThrows(LockedException.class, () -> service.authenticate(requestDTO));

        verify(repository, never()).findByUsername(any());
    }

    @Test
    void authenticate_shouldThrowEntityNotFoundException_whenUserNotFound() {
        doNothing().when(bruteForceProtectionService).checkIfLocked(USERNAME);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.authenticate(requestDTO));

        assertThat(exception.getMessage()).contains("User not found");
        verify(bruteForceProtectionService, never()).loginFailed(any());
    }

    @Test
    void authenticate_shouldThrowBadCredentialsException_whenPasswordInvalid() {
        doNothing().when(bruteForceProtectionService).checkIfLocked(USERNAME);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(userProfileService.checkPassword(INVALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> service.authenticate(requestInvalidPassword));

        assertThat(exception.getMessage()).contains("Invalid credentials");
        verify(bruteForceProtectionService).loginFailed(USERNAME);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void logout_shouldThrowException_whenHeaderIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        UserAuthenticationException exception = assertThrows(UserAuthenticationException.class, () -> service.logout(request));

        assertThat(exception.getMessage()).isEqualTo(INVALID_HEADER_ERROR);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(tokenBlacklistService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        SecurityContextHolder.clearContext();
    }

    @Test
    void logout_shouldThrowException_whenHeaderIsMalformed() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "invalidToken");

        UserAuthenticationException exception = assertThrows(UserAuthenticationException.class, () -> service.logout(request));

        assertThat(exception.getMessage()).isEqualTo(INVALID_HEADER_ERROR);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(tokenBlacklistService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void logout_shouldBlacklistTokenAndClearContext_whenHeaderIsValid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);

        service.logout(request);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService).extractUsername(TOKEN);
        verify(tokenBlacklistService).blacklist(TOKEN);
        SecurityContextHolder.clearContext();
    }
}
