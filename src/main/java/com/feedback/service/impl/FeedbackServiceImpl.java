package com.feedback.service.impl;



import com.feedback.config.JwtUtil;
import com.feedback.dto.FeedbackDTO;
import com.feedback.dto.FeedbackSubmitDTO;
import com.feedback.entity.AuditLog;
import com.feedback.entity.Feedback;
import com.feedback.entity.User;
import com.feedback.mapper.FeedbackMapper;
import com.feedback.repository.AuditLogRepository;
import com.feedback.repository.FeedbackRepository;
import com.feedback.repository.UserRepository;
import com.feedback.service.FeedbackService;
import com.feedback.util.SentimentAnalyzer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final AuditLogRepository auditLogRepository;
    private final FeedbackMapper mapper;
    private final SentimentAnalyzer sentimentAnalyzer;

    @Override
    public FeedbackDTO submitFeedback(String userEmail, FeedbackSubmitDTO dto) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        if (feedbackRepository.findByUser(user).isPresent())
            throw new RuntimeException("User has already submitted feedback");

        Feedback f = Feedback.builder()
                .user(user)
                .q1(dto.getQ1())
                .q2(dto.getQ2())
                .q3(dto.getQ3())
                .userComment(dto.getUserComment())
                .status("SUBMITTED")
                .createdAt(LocalDateTime.now())
                .build();

        var score = sentimentAnalyzer.computeScore(dto.getQ1(), dto.getQ2(), dto.getQ3(), dto.getUserComment());
        f.setSentimentScore(score);
        f.setSentimentLabel(sentimentAnalyzer.label(score));

        Feedback saved = feedbackRepository.save(f);
        auditLogRepository.save(AuditLog.builder()
                .feedbackId(saved.getId())
                .actionByType("USER")
                .actionById(user.getId())
                .actionType("SUBMIT")
                .actionNote("User submitted feedback")
                .build());
        return mapper.toDto(saved);
    }

    @Override
    public FeedbackDTO viewFeedback(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Feedback f = feedbackRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Feedback not found"));
        return mapper.toDto(f);
    }

    @Override
    public FeedbackDTO addAdditionalFeedback(String userEmail, String additional) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Feedback f = feedbackRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Feedback not found"));
        if (!Boolean.TRUE.equals(f.getAllowAdditional())) throw new RuntimeException("Additional feedback not allowed");
        f.setAdditionalFeedback(additional);
        f.setStatus("ADDITIONAL_SUBMITTED");
        f.setUpdatedAt(LocalDateTime.now());
        Feedback updated = feedbackRepository.save(f);
        auditLogRepository.save(AuditLog.builder()
                .feedbackId(updated.getId())
                .actionByType("USER")
                .actionById(user.getId())
                .actionType("ADDITIONAL_SUBMIT")
                .actionNote("User submitted additional feedback")
                .build());
        return mapper.toDto(updated);
    }

    @Override
    public List<FeedbackDTO> getPending() {
        return feedbackRepository.findByAdminCommentIsNullOrderByCreatedAtDesc()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDTO> getReplied() {
        return feedbackRepository.findByAdminCommentIsNotNullOrderByUpdatedAtDesc()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public FeedbackDTO replyToFeedback(String adminEmail, Long feedbackId, String adminComment, boolean allowAdditional) {
        Feedback f = feedbackRepository.findById(feedbackId).orElseThrow(() -> new RuntimeException("Feedback not found"));
        f.setAdminComment(adminComment);
        f.setAllowAdditional(allowAdditional);
        f.setStatus("ADMIN_REPLIED");
        f.setUpdatedAt(LocalDateTime.now());
        Feedback saved = feedbackRepository.save(f);

        auditLogRepository.save(AuditLog.builder()
                .feedbackId(saved.getId())
                .actionByType("ADMIN")
                .actionById(null)
                .actionType("ADMIN_REPLY")
                .actionNote("Admin replied")
                .build());
        return mapper.toDto(saved);
    }
}
