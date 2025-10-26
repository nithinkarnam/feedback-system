package com.feedback.service;

import com.feedback.dto.DashboardDTO;
import com.feedback.dto.FeedbackDTO;
import com.feedback.entity.Admin;

import java.util.List;

public interface AdminService {
    List<FeedbackDTO> listPending();

    List<FeedbackDTO> listReplied();

    FeedbackDTO reply(Long feedbackId, String adminEmail, String comment, boolean allowAdditional);

    Admin registerAdmin(String name, String email, String password);

    DashboardDTO getDashboardStats();
}