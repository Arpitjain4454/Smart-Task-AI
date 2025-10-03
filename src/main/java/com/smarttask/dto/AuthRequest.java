package com.smarttask.dto;
import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String email;    // optional during login, required for signup
    private String password; // plain text password (will be hashed)
}