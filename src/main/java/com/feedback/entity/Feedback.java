package com.feedback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable=false)
    private User user;

    @Column(length=20)
    private String q1; // Yes/No

    @Column(length=20)
    private String q2;

    @Column(length=20)
    private String q3;

    @Column(length = 2000)
    private String userComment;

    @Column(length = 2000)
    private String adminComment;

    private Boolean allowAdditional = false;

    @Column(length = 2000)
    private String additionalFeedback;

    private Double sentimentScore;
    private String sentimentLabel;

    @Column(length=50)
    private String status; // SUBMITTED, ADMIN_REPLIED, ADDITIONAL_SUBMITTED

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
