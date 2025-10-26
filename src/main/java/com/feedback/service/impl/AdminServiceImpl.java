package com.feedback.service.impl;

import com.feedback.dto.DashboardDTO;
import com.feedback.dto.FeedbackDTO;
import com.feedback.entity.Admin;
import com.feedback.repository.AdminRepository;
import com.feedback.repository.FeedbackRepository;
import com.feedback.service.AdminService;
import com.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final FeedbackService feedbackService;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final FeedbackRepository feedbackRepository;

    @Override
    public List<FeedbackDTO> listPending() {
        return feedbackService.getPending();
    }

    @Override
    public List<FeedbackDTO> listReplied() {
        return feedbackService.getReplied();
    }

    @Override
    public FeedbackDTO reply(Long feedbackId, String adminEmail, String comment, boolean allowAdditional) {
        return feedbackService.replyToFeedback(adminEmail, feedbackId, comment, allowAdditional);
    }

    @Override
    public Admin registerAdmin(String name, String email, String password) {
        if (adminRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Admin with this email already exists");
        }

        Admin admin = Admin.builder()
                .username(name)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role("ADMIN") // New admins are regular admins, not super admins
                .build();

        return adminRepository.save(admin);
    }

    @Override
    public DashboardDTO getDashboardStats() {
        long totalFeedbacks = feedbackRepository.count();
        long repliedCount = feedbackRepository.countByAdminCommentIsNotNull();
        long pendingCount = feedbackRepository.countByAdminCommentIsNull();

        List<Double> sentiments = feedbackRepository.findAll()
                .stream()
                .map(f -> f.getSentimentScore())
                .filter(s -> s != null)
                .toList();

        double avgSentiment = sentiments.isEmpty() ? 0.0
                : sentiments.stream().mapToDouble(Double::doubleValue).average().getAsDouble();

        long positiveCount = sentiments.stream().filter(s -> s > 0.3).count();
        long negativeCount = sentiments.stream().filter(s -> s < -0.3).count();
        long neutralCount = sentiments.stream().filter(s -> s >= -0.3 && s <= 0.3).count();

        return DashboardDTO.builder()
                .totalFeedbacks(totalFeedbacks)
                .pendingFeedbacks(pendingCount)
                .repliedFeedbacks(repliedCount)
                .averageSentiment(avgSentiment)
                .positiveCount((int) positiveCount)
                .negativeCount((int) negativeCount)
                .neutralCount((int) neutralCount)
                .build();
    }
}