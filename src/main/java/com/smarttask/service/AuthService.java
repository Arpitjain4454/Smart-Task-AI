package com.smarttask.service;

import com.smarttask.dto.AuthRequest;
import com.smarttask.dto.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    void signup(AuthRequest request);
    AuthResponse login(AuthRequest request);
}
