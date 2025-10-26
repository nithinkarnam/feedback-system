package com.feedback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long feedbackId;
    private String actionByType; // USER or ADMIN
    private Long actionById;
    private String actionType; // SUBMIT, ADMIN_REPLY, REQUEST_ADDITIONAL, ADDITIONAL_SUBMIT
    private String actionNote;
    private LocalDateTime actionTime = LocalDateTime.now();
}
