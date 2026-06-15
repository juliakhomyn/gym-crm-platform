package com.gym.crm.core.service;

import com.gym.crm.core.facade.dto.common.PasswordChangeRequest;
import com.gym.crm.core.facade.dto.common.ToggleActiveRequestDTO;
import com.gym.crm.core.exception.BadCredentialsException;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.exception.ValidationFailedException;
import com.gym.crm.core.model.User;
import com.gym.crm.core.repository.UserRepository;
import com.gym.crm.core.service.impl.UserServiceImpl;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String NON_EXISTENT_USERNAME = "Not.Found";
    private static final String OLD_PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;
    private static final String ENCODED_NEW_PASSWORD = "encodedPassword";
    
    private static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: %s";
    private static final String USER_NOT_FOUND_BY_ID = "User not found by id: %s";
    private static final String USER_ALREADY_ACTIVATED = "Could not activate user %s: user is already activated";

    private final User user = TestDataProvider.buildTraineeUser();
    private final User savedUser = user.toBuilder().id(VALID_ID).build();
    private final PasswordChangeRequest passwordChangeRequest = TestDataProvider.buildPasswordChangeRequest();
    private final ToggleActiveRequestDTO toggleActiveRequestDTO = TestDataProvider.buildToggleActiveRequest();

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(savedUser));

        User actual = service.getById(VALID_ID);

        assertThat(actual).isEqualTo(savedUser);
    }

    @Test
    void getById_shouldThrowException_whenUserNotFound() {
        when(repository.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_ID, NOT_FOUND_ID));
    }

    @Test
    void getByUsername_shouldReturnUser_whenUserExists() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(savedUser));

        User actual = service.getByUsername(USERNAME);

        assertThat(actual).isEqualTo(savedUser);
    }

    @Test
    void getByUsername_shouldThrowException_whenUserNotFound() {
        when(repository.findByUsername(NON_EXISTENT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getByUsername(NON_EXISTENT_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, NON_EXISTENT_USERNAME));
    }

    @Test
    void getAll_shouldReturnAllUsers_whenExist() {
        when(repository.findAll()).thenReturn(List.of(savedUser));

        List<User> actual = service.getAll();

        assertThat(actual).hasSize(1);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoUsers() {
        when(repository.findAll()).thenReturn(List.of());

        List<User> actual = service.getAll();

        assertThat(actual).isEmpty();
    }

    @Test
    void changePassword_shouldUpdatePassword_whenUserValid() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(OLD_PASSWORD, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);

        service.changePassword(passwordChangeRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getPassword()).isEqualTo(ENCODED_NEW_PASSWORD);
        assertThat(updatedUser.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void changePassword_shouldThrowIfUserNotFound() {
        PasswordChangeRequest requestDTO = TestDataProvider.buildInvalidPasswordChangeRequest();
        when(repository.findByUsername(NON_EXISTENT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.changePassword(requestDTO));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, NON_EXISTENT_USERNAME));
    }

    @Test
    void changePassword_shouldThrowIfOldPasswordDoesNotMatch() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(OLD_PASSWORD, user.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> service.changePassword(passwordChangeRequest));

        assertThat(exception.getMessage()).isEqualTo("Existing password does not match with requested one");
        verify(repository, never()).save(any());
    }

    @Test
    void toggleActive_shouldToggleStatus_whenValid() {
        User inactive = user.toBuilder().isActive(false).build();
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(inactive));

        service.toggleActive(TestDataProvider.buildToggleActiveRequest());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getIsActive()).isTrue();
        assertThat(updatedUser.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void toggleActive_shouldThrow_whenUserNotFound() {
        ToggleActiveRequestDTO requestDTO = TestDataProvider.buildToggleActiveRequestNonExistent();
        when(repository.findByUsername(NON_EXISTENT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.toggleActive(requestDTO));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, NON_EXISTENT_USERNAME));
    }

    @Test
    void toggleActive_shouldThrowValidationFailedException_ifStatusIsAlreadySet() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.toggleActive(toggleActiveRequestDTO));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_ALREADY_ACTIVATED, USERNAME));
        verify(repository, never()).save(any());
    }
}
