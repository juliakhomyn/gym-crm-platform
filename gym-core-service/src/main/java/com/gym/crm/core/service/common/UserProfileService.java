package com.gym.crm.core.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    @Transactional(readOnly = true)
    public String generateUsername(String firstName, String lastName) {
        return usernameGenerator.generateUsername(firstName, lastName);
    }

    public String generatePassword() {
        return passwordGenerator.generatePassword();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
