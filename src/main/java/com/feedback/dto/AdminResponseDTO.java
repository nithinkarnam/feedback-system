package com.feedback.dto;

import lombok.Data;

@Data
public class AdminResponseDTO {
    private long totalFeedbacks;
    private long replied;
    private long pending;
}

