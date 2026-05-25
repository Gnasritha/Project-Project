package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.dto.LoginRequest;
import com.aaseya.momthathel.dto.LoginResponse;
import com.aaseya.momthathel.exception.InvalidCredentialsException;
import com.aaseya.momthathel.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Authentication endpoints for the Momthathel mobile app.
 * Single-API username/password login that returns a Keycloak-issued JWT.
 * Errors flow through {@link com.aaseya.momthathel.exception.GlobalExceptionHandler}
 * (4xx mapped from typed exceptions, 5xx as a fallback).
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public LoginResponse currentUser(@RequestHeader(name = "Authorization") String authorizationHeader) {
        return authService.currentUser(extractToken(authorizationHeader));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(name = "Authorization") String authorizationHeader) {
        authService.logout(extractToken(authorizationHeader));
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("status", "success");
        ok.put("message", "Logged out");
        return ResponseEntity.ok(ok);
    }

    private static String extractToken(String header) {
        if (header == null || header.isBlank()) {
            throw new InvalidCredentialsException("Missing Authorization header.");
        }
        String trimmed = header.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }
}
