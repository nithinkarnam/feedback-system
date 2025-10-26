package com.feedback.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class DashboardDTO {
    private long totalFeedbacks;
    private long pendingFeedbacks;
    private long repliedFeedbacks;
    private double averageSentiment;
    private int positiveCount;
    private int negativeCount;
    private int neutralCount;
}