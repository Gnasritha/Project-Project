package com.aaseya.momthathel.dto;

import java.time.LocalDateTime;

public class LoginResponse {

    private Long userId;
    private String username;
    private String fullName;
    private String employeeId;
    private String roleName;
    private String language;
    private String authToken;
    private LocalDateTime tokenExpiresAt;

    private boolean success;
    private String message;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    public LocalDateTime getTokenExpiresAt() { return tokenExpiresAt; }
    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) { this.tokenExpiresAt = tokenExpiresAt; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
