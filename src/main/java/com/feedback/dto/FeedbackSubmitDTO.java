package com.feedback.dto;

import lombok.Data;

@Data
public class FeedbackSubmitDTO {
    // Would you recommend our product/service? (Yes/No)
    private String q1;

    // Would you use our product/service in the future? (Yes/No)
    private String q2;

    // How would you rate our service? (Excellent/Good/Average/Poor)
    private String q3;

    // Additional comments
    private String userComment;
}
