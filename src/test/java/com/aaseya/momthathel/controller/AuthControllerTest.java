package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.BaseIntegrationTest;
import com.aaseya.momthathel.dto.GenerateTokenRequest;
import com.aaseya.momthathel.dto.LoginRequest;
import com.aaseya.momthathel.model.Role;
import com.aaseya.momthathel.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseIntegrationTest {

    private User seedInspector(String username, String status) {
        Role role = testData.createRole("INSPECTOR-" + username);
        User u = testData.createInspector(role, username, "Test " + username, "EMP-" + username, new HashSet<>());
        if (!"ACTIVE".equals(status)) {
            u.setStatus(status);
        }
        return u;
    }

    // ─── POST /api/auth/token/generate ───────────────────────────────────

    @Test
    void generateToken_returnsTokenForActiveUser() throws Exception {
        seedInspector("alice", "ACTIVE");

        GenerateTokenRequest req = new GenerateTokenRequest();
        req.setUsername("alice");
        req.setValidityHours(2);

        mockMvc.perform(post("/api/auth/token/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authToken").isNotEmpty())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void generateToken_returns400WhenUsernameMissing() throws Exception {
        GenerateTokenRequest req = new GenerateTokenRequest();

        mockMvc.perform(post("/api/auth/token/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("username is required"));
    }

    @Test
    void generateToken_returns400WhenUserMissing() throws Exception {
        GenerateTokenRequest req = new GenerateTokenRequest();
        req.setUsername("ghost");

        mockMvc.perform(post("/api/auth/token/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found: ghost"));
    }

    // ─── POST /api/auth/login ────────────────────────────────────────────

    @Test
    void login_succeedsWithValidToken() throws Exception {
        User u = seedInspector("bob", "ACTIVE");
        testData.setUserToken(u.getUserId(), "tok-bob-123", LocalDateTime.now().plusHours(2));

        LoginRequest req = new LoginRequest();
        req.setAuthToken("tok-bob-123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("bob"))
                .andExpect(jsonPath("$.fullName").value("Test bob"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void login_returns401WithInvalidToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setAuthToken("does-not-exist");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(
                        "Invalid token. Please request a new token and try again."));
    }

    @Test
    void login_returns401WithExpiredToken() throws Exception {
        User u = seedInspector("carol", "ACTIVE");
        testData.setUserToken(u.getUserId(), "tok-carol-stale", LocalDateTime.now().minusHours(1));

        LoginRequest req = new LoginRequest();
        req.setAuthToken("tok-carol-stale");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(
                        "Token has expired. Please request a new token and try again."));
    }

    // ─── GET /api/auth/me ────────────────────────────────────────────────

    @Test
    void currentUser_returnsProfileForBearerToken() throws Exception {
        User u = seedInspector("dave", "ACTIVE");
        testData.setUserToken(u.getUserId(), "tok-dave-99", LocalDateTime.now().plusHours(2));

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer tok-dave-99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dave"));
    }

    @Test
    void currentUser_returns401WithoutAuthHeader() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("Authorization", " "))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing Authorization header."));
    }

    // ─── POST /api/auth/logout ───────────────────────────────────────────

    @Test
    void logout_clearsTokenAndReturnsSuccess() throws Exception {
        User u = seedInspector("erin", "ACTIVE");
        testData.setUserToken(u.getUserId(), "tok-erin-99", LocalDateTime.now().plusHours(2));

        mockMvc.perform(post("/api/auth/logout").header("Authorization", "Bearer tok-erin-99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Subsequent /me should now fail
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer tok-erin-99"))
                .andExpect(status().isUnauthorized());
    }
}
