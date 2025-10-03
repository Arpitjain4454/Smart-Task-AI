package com.smarttask.controller;

import com.smarttask.dto.AuthResponse;
import com.smarttask.dto.LoginRequest;
import com.smarttask.dto.SignupRequest;
import com.smarttask.model.User;
import com.smarttask.repository.UserRepository;
import com.smarttask.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    // ✅ Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        // Try to find user by email OR username
        User user = userRepository.findByEmail(req.getIdentifier())
                .orElse(userRepository.findByUsername(req.getIdentifier())
                        .orElseThrow(() -> new RuntimeException("User not found")));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), req.getPassword())
        );
        String token = jwtUtil.generateToken(user.getEmail());
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)  // true in production (HTTPS)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // expire immediately
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }


    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(@CookieValue(value = "jwt", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        String email = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(Map.of("message", "Authorized", "user", email));
    }
}
