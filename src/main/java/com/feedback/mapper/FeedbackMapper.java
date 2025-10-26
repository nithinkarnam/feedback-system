package com.feedback.mapper;

import com.feedback.dto.FeedbackDTO;
import com.feedback.entity.Feedback;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    public FeedbackDTO toDto(Feedback f) {
        if (f == null) return null;
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(f.getId());
        dto.setUserName(f.getUser() != null ? f.getUser().getName() : null);
        dto.setUserEmail(f.getUser() != null ? f.getUser().getEmail() : null);
        dto.setQ1(f.getQ1());
        dto.setQ2(f.getQ2());
        dto.setQ3(f.getQ3());
        dto.setUserComment(f.getUserComment());
        dto.setAdminComment(f.getAdminComment());
        dto.setAllowAdditional(f.getAllowAdditional());
        dto.setAdditionalFeedback(f.getAdditionalFeedback());
        dto.setSentimentScore(f.getSentimentScore());
        dto.setSentimentLabel(f.getSentimentLabel());
        dto.setStatus(f.getStatus());
        dto.setCreatedAt(f.getCreatedAt());
        dto.setUpdatedAt(f.getUpdatedAt());
        return dto;
    }
}
