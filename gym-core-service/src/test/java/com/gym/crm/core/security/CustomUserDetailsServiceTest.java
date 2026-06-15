package com.gym.crm.core.security;

import com.gym.crm.core.model.User;
import com.gym.crm.core.repository.UserRepository;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String NOT_FOUND_USERNAME = "Not.Found";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = TestDataProvider.buildTraineeUser();

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        UserDetails actual = service.loadUserByUsername(USERNAME);

        assertThat(actual).isNotNull();
        assertThat(actual.isEnabled()).isTrue();
        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getPassword()).isEqualTo(user.getPassword());
        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsername_shouldReturnDisabledUserDetails_whenUserIsInactive() {
        User user = TestDataProvider.buildTraineeUser().toBuilder().isActive(false).build();

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        UserDetails actual = service.loadUserByUsername(USERNAME);

        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.isEnabled()).isFalse();
        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findByUsername(NOT_FOUND_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(NOT_FOUND_USERNAME));
        verify(userRepository, times(1)).findByUsername(NOT_FOUND_USERNAME);
    }
}
