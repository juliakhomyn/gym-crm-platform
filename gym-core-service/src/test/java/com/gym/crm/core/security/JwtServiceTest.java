package com.gym.crm.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class JwtServiceTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String JWT_SECRET = "QWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo1Njc4OTAxMjM0NTY3OA==";
    private static final long JWT_EXPIRATION_MS = 3600000;
    
    private final JwtService sut = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sut, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(sut, "jwnExpirationMs", JWT_EXPIRATION_MS);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = sut.generateToken(USERNAME);

        assertThat(token).isNotNull();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = sut.generateToken(USERNAME);

        String actual = sut.extractUsername(token);

        assertThat(actual).isEqualTo(USERNAME);
    }

    @Test
    void extractExpiration_shouldReturnCorrectDate() {
        long now = System.currentTimeMillis();
        String token = sut.generateToken(USERNAME);
        Date expected = new Date(now + JWT_EXPIRATION_MS);

        Date actual = sut.extractExpiration(token);

        assertThat(actual.getTime()).isCloseTo(expected.getTime(), within(2000L));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = sut.generateToken(USERNAME);

        boolean actual = sut.isTokenValid(token);

        assertThat(actual).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";

        boolean actual = sut.isTokenValid(invalidToken);

        assertThat(actual).isFalse();
    }
}
