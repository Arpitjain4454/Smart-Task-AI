package com.smarttask.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String passwordHash;
    private String role = "USER";

    private LocalDateTime createdAt = LocalDateTime.now();
}
