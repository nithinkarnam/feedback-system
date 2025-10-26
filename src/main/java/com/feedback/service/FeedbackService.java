package com.feedback.service;

import com.feedback.dto.FeedbackDTO;
import com.feedback.dto.FeedbackSubmitDTO;

import java.util.List;

public interface FeedbackService {
    FeedbackDTO submitFeedback(String userEmail, FeedbackSubmitDTO dto);
    FeedbackDTO viewFeedback(String userEmail);
    FeedbackDTO addAdditionalFeedback(String userEmail, String additional);
    List<FeedbackDTO> getPending();
    List<FeedbackDTO> getReplied();
    FeedbackDTO replyToFeedback(String adminEmail, Long feedbackId, String adminComment, boolean allowAdditional);
}
