package com.feedback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String passwordHash;

    @Column(nullable=false)
    private String role; // ADMIN or SUPER_ADMIN

    private LocalDateTime createdAt = LocalDateTime.now();
}
