package com.feedback.service;

import com.feedback.dto.DashboardDTO;
import com.feedback.entity.Admin;
import com.feedback.entity.Feedback;
import com.feedback.repository.AdminRepository;
import com.feedback.repository.FeedbackRepository;
import com.feedback.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private FeedbackService feedbackService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminServiceImpl(feedbackService, adminRepository, passwordEncoder, feedbackRepository);
    }

    @Test
    void registerAdmin_Success() {
        // Arrange
        String name = "Test Admin";
        String email = "admin@test.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";

        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(adminRepository.save(any(Admin.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Admin result = adminService.registerAdmin(name, email, password);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(hashedPassword, result.getPasswordHash());
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void getDashboardStats_Success() {
        // Arrange
        when(feedbackRepository.count()).thenReturn(5L);
        when(feedbackRepository.countByAdminCommentIsNull()).thenReturn(2L);
        when(feedbackRepository.countByAdminCommentIsNotNull()).thenReturn(3L);

        Feedback f1 = Feedback.builder().sentimentScore(0.8).build();
        Feedback f2 = Feedback.builder().sentimentScore(-0.5).build();
        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        // Act
        DashboardDTO result = adminService.getDashboardStats();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getTotalFeedbacks());
        assertEquals(2, result.getPendingFeedbacks());
        assertEquals(3, result.getRepliedFeedbacks());
        assertTrue(result.getAverageSentiment() != 0.0);
    }
}