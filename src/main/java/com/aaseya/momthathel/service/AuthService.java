package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dao.UserRepository;
import com.aaseya.momthathel.dto.LoginRequest;
import com.aaseya.momthathel.dto.LoginResponse;
import com.aaseya.momthathel.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Authentication for the Momthathel mobile app.
 *
 * <p>Single-step login: client posts {@code username + password} → backend verifies
 * the bcrypt-hashed password → issues an opaque session token + returns user profile.
 * Token is required (as Bearer) for subsequent calls to {@code /api/auth/me} and
 * {@code /api/auth/logout}.</p>
 */
@Service
public class AuthService {

    private static final int SESSION_VALIDITY_HOURS = 24;
    private static final int TOKEN_BYTE_LENGTH = 48;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public LoginResponse login(LoginRequest request) {

        LoginResponse response = new LoginResponse();

        try {
            if (request.getUsername() == null || request.getUsername().isBlank()) {
                throw new RuntimeException("username is required");
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new RuntimeException("password is required");
            }

            User user = userRepository.findByUsername(request.getUsername());
            if (user == null) {
                throw new RuntimeException("Invalid username or password");
            }

            if (user.getStatus() != null && !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                throw new RuntimeException("User account is not active");
            }

            if (user.getPasswordHash() == null
                    || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new RuntimeException("Invalid username or password");
            }

            String token = generateOpaqueToken();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plusHours(SESSION_VALIDITY_HOURS);

            user.setAuthToken(token);
            user.setTokenGeneratedAt(now);
            user.setTokenExpiresAt(expiresAt);
            userRepository.saveOrUpdate(user);

            String resolvedLanguage = (request.getLanguage() != null && !request.getLanguage().isBlank())
                    ? request.getLanguage()
                    : user.getLanguage();

            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            response.setFullName(user.getFullName());
            response.setEmployeeId(user.getEmployeeId());
            response.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
            response.setLanguage(resolvedLanguage);
            response.setAuthToken(token);
            response.setTokenExpiresAt(expiresAt);
            response.setSuccess(true);
            response.setMessage("Login successful");

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public LoginResponse currentUser(String authToken) {

        LoginResponse response = new LoginResponse();

        try {
            if (authToken == null || authToken.isBlank()) {
                throw new RuntimeException("Missing token");
            }

            User user = userRepository.findByAuthToken(authToken);
            if (user == null) {
                throw new RuntimeException("Invalid token");
            }
            if (user.getTokenExpiresAt() == null || user.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token has expired. Please log in again.");
            }
            if (user.getStatus() != null && !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                throw new RuntimeException("User account is not active");
            }

            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            response.setFullName(user.getFullName());
            response.setEmployeeId(user.getEmployeeId());
            response.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
            response.setLanguage(user.getLanguage());
            response.setAuthToken(user.getAuthToken());
            response.setTokenExpiresAt(user.getTokenExpiresAt());
            response.setSuccess(true);
            response.setMessage("OK");

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void logout(String authToken) {
        try {
            User user = userRepository.findByAuthToken(authToken);
            if (user != null) {
                user.setAuthToken(null);
                user.setTokenGeneratedAt(null);
                user.setTokenExpiresAt(null);
                userRepository.saveOrUpdate(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Re-hash a plaintext password — used by {@code AuthInitializer} to seed
     * real bcrypt hashes for users that were created with a placeholder.
     */
    public String hashPassword(String plaintext) {
        return passwordEncoder.encode(plaintext);
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
