package com.feedback.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long validityInMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expirationMs}") long validityInMs) {
        // Ensure the signing key is at least 256 bits (32 bytes) as required by JJWT
        // for HS256.
        // Derive a 256-bit key deterministically from the configured secret by taking
        // its SHA-256 hash.
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            this.key = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // In the unlikely event SHA-256 is not available, fall back to using the raw
            // bytes
            // (JJWT will still throw if key is too weak). Re-throw as a runtime exception
            // to
            // fail fast during bean creation with a clear message.
            throw new IllegalStateException("Unable to initialize JWT signing key", e);
        }
        this.validityInMs = validityInMs;
    }

    public String generateToken(String subject, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRole(String token) {
        Claims c = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        Object role = c.get("role");
        return role != null ? role.toString() : null;
    }
}
