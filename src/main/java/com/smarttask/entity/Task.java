package com.smarttask.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String assignedTo; // username or email
    private String createdBy;
    private String status = "PENDING"; // default
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters
}
