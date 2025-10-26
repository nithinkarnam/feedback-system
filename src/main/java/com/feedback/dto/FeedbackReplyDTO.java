package com.feedback.dto;

import lombok.Data;

@Data
public class FeedbackReplyDTO {
    private Long feedbackId;
    private String adminComment;
    private Boolean allowAdditional;
}
