package com.feedback.controller;

import com.feedback.dto.DashboardDTO;
import com.feedback.dto.FeedbackDTO;
import com.feedback.dto.FeedbackReplyDTO;
import com.feedback.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/feedbacks/pending")
    public ResponseEntity<List<FeedbackDTO>> pending() {
        return ResponseEntity.ok(adminService.listPending());
    }

    @GetMapping("/feedbacks/replied")
    public ResponseEntity<List<FeedbackDTO>> replied() {
        return ResponseEntity.ok(adminService.listReplied());
    }

    @PutMapping("/feedbacks/reply")
    public ResponseEntity<FeedbackDTO> reply(@RequestBody FeedbackReplyDTO dto) {
        // NOTE: The 'null' for adminEmail in the original implementation suggests
        // that the email is likely retrieved from the SecurityContext (Authentication
        // object)
        // in a real application, but keeping the signature as is for path change.
        FeedbackDTO updated = adminService.reply(dto.getFeedbackId(), null, dto.getAdminComment(),
                dto.getAllowAdditional());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}