package com.feedback.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackDTO {
    private Long id;
    private String userName;
    private String userEmail;
    private String q1;
    private String q2;
    private String q3;
    private String userComment;
    private String adminComment;
    private Boolean allowAdditional;
    private String additionalFeedback;
    private Double sentimentScore;
    private String sentimentLabel;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
