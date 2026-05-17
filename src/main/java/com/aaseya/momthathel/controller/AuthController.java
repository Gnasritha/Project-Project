package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.dto.LoginRequest;
import com.aaseya.momthathel.dto.LoginResponse;
import com.aaseya.momthathel.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication endpoints for the Momthathel mobile app.
 * Single-step username/password login that returns a Bearer token.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse result = authService.login(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(@RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            LoginResponse result = authService.currentUser(extractToken(authorizationHeader));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            authService.logout(extractToken(authorizationHeader));
            Map<String, Object> ok = new HashMap<>();
            ok.put("status", "success");
            ok.put("message", "Logged out");
            return ResponseEntity.ok(ok);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private static String extractToken(String header) {
        if (header == null || header.isBlank()) {
            throw new RuntimeException("Missing Authorization header.");
        }
        String trimmed = header.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }
}
