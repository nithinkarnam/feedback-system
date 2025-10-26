package com.feedback.util;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SentimentAnalyzer {

    private final Set<String> positive = Set.of(
            "good", "great", "helpful", "excellent", "awesome", "nice",
            "satisfied", "happy", "thank", "appreciation", "wonderful",
            "perfect", "fantastic", "amazing", "love", "best");
    private final Set<String> negative = Set.of(
            "bad", "poor", "slow", "terrible", "awful", "disappoint",
            "worse", "worst", "horrible", "hate", "useless", "waste",
            "problem", "issue", "difficult", "confusing");

    public double computeScore(String q1, String q2, String q3, String text) {
        double s1 = mapYesNo(q1);
        double s2 = mapYesNo(q2);
        double s3 = mapRating(q3);
        double mcqScore = (s1 + s2 + s3) / 3.0;
        double textScore = computeTextScore(text);
        return 0.8 * mcqScore + 0.2 * textScore;
    }

    private double mapYesNo(String v) {
        if (v == null)
            return 0;
        v = v.trim().toLowerCase();
        return switch (v) {
            case "yes" -> 1.0;
            case "no" -> -1.0;
            default -> 0.0;
        };
    }

    private double mapRating(String v) {
        if (v == null)
            return 0;
        v = v.trim().toLowerCase();
        return switch (v) {
            case "excellent" -> 1.0;
            case "good" -> 0.5;
            case "average" -> 0.0;
            case "poor" -> -1.0;
            default -> 0.0;
        };
    }

    private double computeTextScore(String text) {
        if (text == null)
            return 0.0;
        String t = text.toLowerCase();
        int pos = 0, neg = 0;
        for (var p : positive)
            if (t.contains(p))
                pos++;
        for (var n : negative)
            if (t.contains(n))
                neg++;
        if (pos + neg == 0)
            return 0.0;
        return (double) (pos - neg) / (pos + neg);
    }

    public String label(double score) {
        if (score > 0.3)
            return "Positive";
        if (score < -0.3)
            return "Negative";
        return "Neutral";
    }
}