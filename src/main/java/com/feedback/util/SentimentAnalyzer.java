package com.feedback.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class SentimentAnalyzer {

    // Expanded and slightly normalized lexicons for broader coverage
    private final Set<String> positive = new HashSet<>(Arrays.asList(
            "good", "great", "helpful", "excellent", "awesome", "nice",
            "satisfied", "happy", "thank", "thanks", "appreciate", "appreciation", "wonderful",
            "perfect", "fantastic", "amazing", "love", "best", "recommend", "pleased",
            "responsive", "fast", "reliable", "clear", "easy", "smooth"));

    private final Set<String> negative = new HashSet<>(Arrays.asList(
            "bad", "poor", "slow", "terrible", "awful", "disappoint", "disappointed",
            "worse", "worst", "horrible", "hate", "useless", "waste", "problem", "issue",
            "difficult", "confusing", "broken", "bug", "delay", "delayd", "unreliable", "hard"));

    // Negation words that invert nearby sentiment. Include common contractions and
    // misspellings.
    private final Set<String> negations = new HashSet<>(Arrays.asList(
            "not", "no", "never", "none", "n't", "dont", "don't", "doesnt", "doesn't",
            "didnt", "didn't", "cannot", "cant", "can't", "couldnt", "couldn't", "won't", "wont",
            "wouldnt", "wouldn't", "isnt", "isn't", "aren't", "arent"));

    // Intensifiers and dampeners to adjust sentiment weight (single-token forms)
    private final Set<String> intensifiers = new HashSet<>(Arrays.asList(
            "very", "extremely", "absolutely", "really", "highly", "totally", "so"));
    private final Set<String> dampeners = new HashSet<>(Arrays.asList(
            "slightly", "somewhat", "bit", "little"));

    private static final Pattern TOKEN_PATTERN = Pattern.compile("[a-zA-Z0-9]+(?:'[a-z]+)?");

    public double computeScore(String q1, String q2, String q3, String text) {
        double s1 = mapYesNo(q1);
        double s2 = mapYesNo(q2);
        double s3 = mapRating(q3);
        double mcqScore = (s1 + s2 + s3) / 3.0;
        double textScore = computeTextScore(text);
        // Keep the original weighting so endpoints and integration behavior remain
        // stable
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

    /**
     * Improved text scoring:
     * - Tokenizes input
     * - Applies weights for intensifiers / dampeners
     * - Handles simple negation (inverts sentiment for nearby words)
     * - Produces a normalized score in [-1, 1]
     */
    private double computeTextScore(String text) {
        if (text == null || text.isBlank())
            return 0.0;

        String lower = text.toLowerCase();

        // Tokenize using regex to keep contractions like don't
        List<String> tokens = new ArrayList<>();
        var m = TOKEN_PATTERN.matcher(lower);
        while (m.find())
            tokens.add(m.group());

        if (tokens.isEmpty())
            return 0.0;

        double posScore = 0.0;
        double negScore = 0.0;

        for (int i = 0; i < tokens.size(); i++) {
            String tk = tokens.get(i);

            double weight = 1.0;
            // look back one or two tokens for intensifiers/dampeners (smaller effect)
            if (i - 1 >= 0) {
                String prev = tokens.get(i - 1);
                if (intensifiers.contains(prev))
                    weight *= 1.3;
                if (dampeners.contains(prev))
                    weight *= 0.75;
            }
            if (i - 2 >= 0) {
                String prev2 = tokens.get(i - 2);
                if (intensifiers.contains(prev2))
                    weight *= 1.1;
            }

            // cap weight to avoid extreme influence
            weight = Math.min(weight, 2.0);

            boolean negated = false;
            // check a larger window before the token for negation (handles "I don't feel
            // this is good" etc.)
            for (int j = Math.max(0, i - 5); j < i; j++) {
                if (negations.contains(tokens.get(j))) {
                    negated = true;
                    break;
                }
            }

            if (positive.contains(tk)) {
                if (negated)
                    negScore += weight;
                else
                    posScore += weight;
            } else if (negative.contains(tk)) {
                if (negated)
                    posScore += weight;
                else
                    negScore += weight;
            }
        }

        double sum = posScore + negScore;
        if (sum == 0.0)
            return 0.0;
        // normalized difference in [-1, 1]
        return (posScore - negScore) / sum;
    }

    public String label(double score) {
        if (score > 0.3)
            return "Positive";
        if (score < -0.3)
            return "Negative";
        return "Neutral";
    }
}