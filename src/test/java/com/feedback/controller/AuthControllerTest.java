package com.feedback.controller;

import com.feedback.dto.AuthResponse;
import com.feedback.dto.LoginRequest;
import com.feedback.entity.Admin;
import com.feedback.repository.AdminRepository;
import com.feedback.repository.UserRepository;
import com.feedback.service.AdminService;
import com.feedback.config.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AdminService adminService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(adminRepository, userRepository, passwordEncoder, jwtUtil, adminService);
    }

    @Test
    void adminLogin_Success() {
        // Arrange
        String email = "admin@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";
        String token = "jwt-token";
        String role = "ADMIN";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        Admin admin = Admin.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .role(role)
                .build();

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(email, role)).thenReturn(token);

        // Act
        ResponseEntity<AuthResponse> response = authController.adminLogin(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().getToken());
        assertEquals(role, response.getBody().getRole());
        assertEquals(email, response.getBody().getEmail());
    }

    @Test
    void adminLogin_InvalidCredentials() {
        // Arrange
        String email = "admin@example.com";
        String password = "wrongpassword";
        String hashedPassword = "correcthash";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        Admin admin = Admin.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .build();

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.adminLogin(request));
    }
}