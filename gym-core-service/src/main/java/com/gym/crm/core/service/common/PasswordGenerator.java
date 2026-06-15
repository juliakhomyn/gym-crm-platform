package com.gym.crm.core.service.common;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {
    private static final int PASSWORD_LENGTH = 10;
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvxyz"
            + "0123456789";

    private final SecureRandom random = new SecureRandom();

    public String generatePassword() {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARS.length());
            password.append(CHARS.charAt(index));
        }

        return password.toString();
    }
}
