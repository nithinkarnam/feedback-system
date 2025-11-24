package com.feedback.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    @Test
    void generateAndValidateToken() {
        String secret = "test-secret-key-for-jwt-signing-which-is-long-enough";
        long validity = 3600_000; // 1 hour
        JwtTokenProvider provider = new JwtTokenProvider(secret, validity);

        String token = provider.generateToken("user@example.com", "USER");
        assertNotNull(token);
        assertTrue(provider.validateToken(token));
        assertEquals("user@example.com", provider.getSubject(token));
        assertEquals("USER", provider.getRole(token));
    }
}
