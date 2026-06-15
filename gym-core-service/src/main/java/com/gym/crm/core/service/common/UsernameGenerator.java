package com.gym.crm.core.service.common;

import com.gym.crm.core.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class UsernameGenerator {
    private static final String SEPARATOR = ".";

    private final UserRepository repository;

    public String generateUsername(@NotBlank String firstName, @NotBlank String lastName) {
        String baseUsername = (firstName + SEPARATOR + lastName);

        if (!repository.existsByUsername(baseUsername)) {
            return baseUsername;
        }

        long serialNumber = 1;
        while (repository.existsByUsername(baseUsername + serialNumber)) {
            serialNumber++;
        }

        log.warn("Username {} already exists, serial number {} will be appended", baseUsername, serialNumber);
        return baseUsername + serialNumber;
    }
}
