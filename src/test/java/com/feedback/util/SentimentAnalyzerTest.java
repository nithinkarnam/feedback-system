package com.feedback.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SentimentAnalyzerTest {

    private SentimentAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new SentimentAnalyzer();
    }

    @Test
    void testPositiveSentiment() {
        // Test with all positive responses
        double score = analyzer.computeScore("Yes", "Yes", "Excellent", "The service was excellent and very helpful");
        assertTrue(score > 0.3);
        assertEquals("Positive", analyzer.label(score));
    }

    @Test
    void testNegativeSentiment() {
        // Test with all negative responses
        double score = analyzer.computeScore("No", "No", "Poor", "The service was terrible");
        assertTrue(score < -0.3);
        assertEquals("Negative", analyzer.label(score));
    }

    @Test
    void testNeutralSentiment() {
        // Test with mixed responses
        double score = analyzer.computeScore("Yes", "No", "Average", "The service was okay");
        assertTrue(score >= -0.3 && score <= 0.3);
        assertEquals("Neutral", analyzer.label(score));
    }
}