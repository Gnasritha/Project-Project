package com.aaseya.momthathel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mobile-app login response. Returned shape (superset of the LMS-reference
 * minimal shape + the local profile fields the mobile contract already
 * expects):
 * <ul>
 *   <li>{@code accessToken}, {@code tokenType}, {@code expiresIn},
 *       {@code roles} — straight from the Keycloak token + JWT claims</li>
 *   <li>{@code authToken} — alias of {@code accessToken} kept for the
 *       existing mobile contract</li>
 *   <li>{@code userId}, {@code username}, {@code fullName},
 *       {@code employeeId}, {@code roleName}, {@code language},
 *       {@code tokenExpiresAt} — local-profile fields downstream APIs use</li>
 *   <li>{@code success}, {@code message} — kept for backwards compat</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    // ── LMS-style token fields ───────────────────────────────────────────
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private List<String> roles;

    // ── Local profile fields (existing mobile contract) ─────────────────
    private Long userId;
    private String username;
    private String fullName;
    private String employeeId;
    private String roleName;
    private String language;

    // ── Legacy / convenience ────────────────────────────────────────────
    private String authToken;          // alias of accessToken
    private LocalDateTime tokenExpiresAt;

    private boolean success;
    private String message;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

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
