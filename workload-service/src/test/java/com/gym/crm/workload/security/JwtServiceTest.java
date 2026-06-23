package com.gym.crm.workload.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String JWT_SECRET = "TestSecret777mtsbW5vcHFyc3R1dnd4eXo1Njc4OTAxMjM0NTY3OA==";
    private static final long JWT_EXPIRATION_MS = 360000;

    private final JwtService sut = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sut, "jwtSecret", JWT_SECRET);
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = generateToken(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS));

        String actual = sut.extractUsername(token);

        assertThat(actual).isEqualTo(USERNAME);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = generateToken(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS));

        boolean actual = sut.isTokenValid(token);

        assertThat(actual).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";

        boolean actual = sut.isTokenValid(invalidToken);

        assertThat(actual).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        String token = generateToken(new Date(System.currentTimeMillis() - 1000));

        boolean actual = sut.isTokenValid(token);

        assertThat(actual).isFalse();
    }

    private String generateToken(Date expiration) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));

        return Jwts.builder()
                .subject(USERNAME)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}
