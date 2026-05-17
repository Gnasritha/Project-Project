package com.aaseya.momthathel.dto;

import java.time.LocalDateTime;

public class GenerateTokenResponse {

    private String authToken;
    private String username;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;

    private boolean success;
    private String message;

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
