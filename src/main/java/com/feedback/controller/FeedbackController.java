package com.feedback.controller;

import com.feedback.dto.FeedbackDTO;
import com.feedback.dto.FeedbackSubmitDTO;
import com.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<FeedbackDTO> submit(Authentication auth, @RequestBody FeedbackSubmitDTO dto) {
        String email = (String) auth.getPrincipal();
        FeedbackDTO created = feedbackService.submitFeedback(email, dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/my")
    public ResponseEntity<FeedbackDTO> my(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return ResponseEntity.ok(feedbackService.viewFeedback(email));
    }

    @PutMapping("/additional")
    public ResponseEntity<FeedbackDTO> additional(Authentication auth, @RequestBody String additional) {
        String email = (String) auth.getPrincipal();
        return ResponseEntity.ok(feedbackService.addAdditionalFeedback(email, additional));
    }
}