package com.feedback.service;

import com.feedback.dto.FeedbackDTO;
import com.feedback.dto.FeedbackSubmitDTO;
import com.feedback.entity.Feedback;
import com.feedback.entity.User;
import com.feedback.repository.FeedbackRepository;
import com.feedback.repository.UserRepository;
import com.feedback.service.impl.FeedbackServiceImpl;
import com.feedback.mapper.FeedbackMapper;
import com.feedback.repository.AuditLogRepository;
import com.feedback.util.SentimentAnalyzer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FeedbackMapper mapper;
    @Mock
    private SentimentAnalyzer sentimentAnalyzer;

    private FeedbackService feedbackService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        feedbackService = new FeedbackServiceImpl(userRepository, feedbackRepository, auditLogRepository, mapper,
                sentimentAnalyzer);
    }

    @Test
    void submitFeedback_Success() {
        // Arrange
        String userEmail = "test@example.com";
        User user = User.builder()
                .id(1L)
                .email(userEmail)
                .name("Test User")
                .build();

        FeedbackSubmitDTO submitDTO = new FeedbackSubmitDTO();
        submitDTO.setQ1("Yes");
        submitDTO.setQ2("Yes");
        submitDTO.setQ3("Excellent");
        submitDTO.setUserComment("Great service");

        Feedback savedFeedback = Feedback.builder()
                .id(1L)
                .user(user)
                .q1("Yes")
                .q2("Yes")
                .q3("Excellent")
                .userComment("Great service")
                .status("SUBMITTED")
                .build();

        FeedbackDTO expectedDTO = new FeedbackDTO();
        expectedDTO.setId(1L);
        expectedDTO.setUserEmail(userEmail);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(feedbackRepository.findByUser(user)).thenReturn(Optional.empty());
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);
        when(mapper.toDto(savedFeedback)).thenReturn(expectedDTO);
        when(sentimentAnalyzer.computeScore("Yes", "Yes", "Excellent", "Great service")).thenReturn(0.8);

        // Act
        FeedbackDTO result = feedbackService.submitFeedback(userEmail, submitDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userEmail, result.getUserEmail());
        assertEquals(1L, result.getId());
    }

    @Test
    void submitFeedback_UserNotFound() {
        // Arrange
        String userEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> feedbackService.submitFeedback(userEmail, new FeedbackSubmitDTO()));
    }
}