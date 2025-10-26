package com.feedback.repository;

import com.feedback.entity.Feedback;
import com.feedback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByUser(User user);

    List<Feedback> findByAdminCommentIsNullOrderByCreatedAtDesc();

    List<Feedback> findByAdminCommentIsNotNullOrderByUpdatedAtDesc();

    long countByAdminCommentIsNull();

    long countByAdminCommentIsNotNull();
}
