package com.gym.crm.core.service.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    private static final String FIRST_NAME = "Cillian";
    private static final String LAST_NAME = "Mercer";
    private static final String USERNAME = "Cillian.Mercer";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UsernameGenerator usernameGenerator;

    @InjectMocks
    private UserProfileService service;

    @Test
    void generateUsername_shouldReturnConcatenatedUsername() {
        when(usernameGenerator.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);

        String actual = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertThat(actual).isEqualTo(USERNAME);
        verify(usernameGenerator).generateUsername(FIRST_NAME, LAST_NAME);
    }
    @Test
    void generatePassword_shouldReturnRawPassword() {
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);

        String actual = service.generatePassword();

        assertThat(actual).isEqualTo(PASSWORD);
        verify(passwordGenerator).generatePassword();
    }

    @Test
    void encodePassword_shouldReturnEncodedPassword() {
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);

        String actual = service.encodePassword(PASSWORD);

        assertThat(actual).isEqualTo(ENCODED_PASSWORD);
        verify(passwordEncoder).encode(PASSWORD);
    }
}
