package com.gym.crm.core.service.impl;

import com.gym.crm.core.facade.dto.common.PasswordChangeRequest;
import com.gym.crm.core.facade.dto.common.ToggleActiveRequestDTO;
import com.gym.crm.core.exception.BadCredentialsException;
import com.gym.crm.core.exception.ValidationFailedException;
import com.gym.crm.core.model.User;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.repository.UserRepository;
import com.gym.crm.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_BY_ID = "User not found by id: %s";
    private static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: %s";

    private final PasswordEncoder passwordEncoder;

    private final UserRepository repository;

    @Transactional(readOnly = true)
    @Override
    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, username)));
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_ID, id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public void changePassword(PasswordChangeRequest request) {
        log.info("Changing password for user: username={}", request.getUsername());

        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, request.getUsername())));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Existing password does not match with requested password for user: username={}", request.getUsername());
            throw new BadCredentialsException("Existing password does not match with requested one");
        }

        User userWithNewPassword = user.toBuilder()
                .password(passwordEncoder.encode(request.getNewPassword()))
                .build();
        repository.save(userWithNewPassword);
        log.info("Changed password for user: username={}", request.getUsername());
    }

    @Transactional
    @Override
    public void toggleActive(ToggleActiveRequestDTO request) {
        log.info("Changing active status for user: username={}", request.getUsername());

        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, request.getUsername())));

        boolean currentStatus = user.getIsActive();
        if (currentStatus == request.isActive()) {
            String status = currentStatus ? "activate" : "deactivate";
            log.warn("Could not {} user: username={} is already {}d", status, request.getUsername(), status);

            throw new ValidationFailedException(String.format("Could not %s user %s: user is already %sd", status, request.getUsername(), status));
        }

        User userWithChangedStatus = user.toBuilder()
                .isActive(request.isActive())
                .build();

        repository.save(userWithChangedStatus);
        log.info("User {}: username={}", currentStatus ? "deactivated" : "activated", request.getUsername());
    }
}
