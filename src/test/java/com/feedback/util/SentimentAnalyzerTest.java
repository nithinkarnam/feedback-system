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

    @Test
    void testNegationPhrase() {
        // Neutralize MCQ inputs so we test text-only effect: negation should push text
        // score negative
        double score = analyzer.computeScore(null, null, "Average", "I don't feel this is good");
        assertTrue(score < 0.0, "Expected negative overall score due to negation in text");
    }

    @Test
    void testIntensifierAndDampener() {
        // Neutral MCQ so only text contributes — intensifier should increase text score
        // vs dampener
        double scoreVery = analyzer.computeScore(null, null, "Average", "This is very good and excellent");
        double scoreSlight = analyzer.computeScore(null, null, "Average", "This is slightly good");
        assertTrue(scoreVery >= scoreSlight, "Intensifier should not be less than dampened version");
    }

    @Test
    void testMixedOpinion() {
        double score = analyzer.computeScore("Yes", "No", "Average", "Good but slow and confusing");
        // Mixed should be near neutral but not extreme
        assertTrue(score > -0.6 && score < 0.6);
    }
}