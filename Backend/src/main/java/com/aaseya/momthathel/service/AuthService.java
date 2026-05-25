package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dao.UserRepository;
import com.aaseya.momthathel.dto.KeycloakTokenResponse;
import com.aaseya.momthathel.dto.LoginRequest;
import com.aaseya.momthathel.dto.LoginResponse;
import com.aaseya.momthathel.exception.ForbiddenException;
import com.aaseya.momthathel.exception.InvalidCredentialsException;
import com.aaseya.momthathel.model.Role;
import com.aaseya.momthathel.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Authentication backed by Keycloak — single API, ROPC flow (mirrors the
 * LMS-reference pattern).
 *
 * <p>Flow: mobile posts {username, password} → forwards to Keycloak's token
 * endpoint → on success we verify the realm role, upsert the local
 * {@code users} row from JWT claims, and return the Keycloak access token
 * as the auth token.</p>
 *
 * <p>The local {@code users} row is the FK target for {@link com.aaseya.momthathel.model.InspectionCase},
 * attachments, etc., so it must exist for every authenticated inspector.
 * It is auto-provisioned on first login. If the required local {@link Role}
 * row is missing, it is auto-created so a fresh Keycloak user can log in
 * against an unseeded {@code roles} table.</p>
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeycloakUserService keycloakUserService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${keycloak.required-role:ROLE_INSPECTOR}")
    private String requiredRole;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public LoginResponse login(LoginRequest request) {
        KeycloakTokenResponse token = keycloakUserService.inspectorLogin(request);

        Map<String, Object> claims = decodeJwtPayload(token.accessToken());
        List<String> realmRoles = extractRealmRoles(claims);
        if (!hasRequiredRole(realmRoles)) {
            throw new ForbiddenException("User is not authorised (missing role " + requiredRole + ")");
        }

        User user = upsertLocalUser(claims, request.language());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(token.expiresIn());

        return buildResponse(user, token, realmRoles, expiresAt, "Login successful");
    }

    @Transactional(readOnly = true)
    public LoginResponse currentUser(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new InvalidCredentialsException("Missing token");
        }

        Map<String, Object> claims = decodeJwtPayload(accessToken);

        Long exp = claims.get("exp") != null ? ((Number) claims.get("exp")).longValue() : null;
        if (exp != null && exp * 1000L < System.currentTimeMillis()) {
            throw new InvalidCredentialsException("Token has expired. Please log in again.");
        }

        User user = findUserFromClaims(claims);
        if (user == null) {
            throw new InvalidCredentialsException("User not found locally — please log in again");
        }

        LocalDateTime expiresAt = exp != null
                ? LocalDateTime.ofInstant(Instant.ofEpochSecond(exp), ZoneId.systemDefault())
                : null;

        List<String> realmRoles = extractRealmRoles(claims);
        long expiresIn = exp != null ? Math.max(0, exp - (System.currentTimeMillis() / 1000L)) : 0L;
        KeycloakTokenResponse synthetic = new KeycloakTokenResponse(accessToken, null, expiresIn, "Bearer");

        return buildResponse(user, synthetic, realmRoles, expiresAt, "OK");
    }

    /**
     * Stateless JWT — there is no server-side session to invalidate.
     * The client deletes the token. We accept the call so the existing
     * mobile contract keeps working.
     */
    public void logout(String accessToken) {
        // no-op
    }

    // ────────────────────────────────────────────────────────────────────
    //  Helpers
    // ────────────────────────────────────────────────────────────────────

    private boolean hasRequiredRole(List<String> realmRoles) {
        String stripped = strippedRequiredRole();
        for (String r : realmRoles) {
            if (r.equals(requiredRole) || r.equals(stripped)) {
                return true;
            }
        }
        return false;
    }

    private User upsertLocalUser(Map<String, Object> claims, String requestedLanguage) {
        String sub = (String) claims.get("sub");
        String username = (String) claims.getOrDefault("preferred_username", sub);
        String email = (String) claims.get("email");
        String fullName = (String) claims.get("name");
        if (fullName == null || fullName.isBlank()) {
            String first = (String) claims.getOrDefault("given_name", "");
            String last = (String) claims.getOrDefault("family_name", "");
            fullName = (first + " " + last).trim();
        }
        if (fullName.isBlank()) {
            fullName = username;
        }

        User user = sub != null ? findByKeycloakSub(sub) : null;
        if (user == null) {
            user = userRepository.findByUsername(username);
        }

        boolean isNew = (user == null);
        if (isNew) {
            user = new User();
            user.setUsername(username);
            user.setStatus("ACTIVE");
            user.setRole(findOrCreateRequiredRole());
        }

        user.setKeycloakSub(sub);
        user.setFullName(fullName);
        if (email != null) {
            user.setEmail(email);
        }
        if (requestedLanguage != null && !requestedLanguage.isBlank()) {
            user.setLanguage(requestedLanguage);
        }

        userRepository.saveOrUpdate(user);
        return user;
    }

    private User findUserFromClaims(Map<String, Object> claims) {
        String sub = (String) claims.get("sub");
        if (sub != null) {
            User u = findByKeycloakSub(sub);
            if (u != null) return u;
        }
        String username = (String) claims.get("preferred_username");
        if (username != null) {
            return userRepository.findByUsername(username);
        }
        return null;
    }

    private User findByKeycloakSub(String sub) {
        List<User> result = entityManager
                .createQuery("SELECT u FROM User u WHERE u.keycloakSub = :sub", User.class)
                .setParameter("sub", sub)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Maps the Keycloak realm role to a row in the local {@code roles} table.
     * Strips a {@code ROLE_} prefix when present so a JWT realm role
     * {@code INSPECTOR} matches the local row {@code INSPECTOR}.
     * Auto-creates the local row if missing — a freshly-provisioned Keycloak
     * user can therefore log in without a manual DB seed.
     */
    private Role findOrCreateRequiredRole() {
        String stripped = strippedRequiredRole();
        List<Role> result = entityManager
                .createQuery("SELECT r FROM Role r WHERE r.roleName = :name OR r.roleName = :raw", Role.class)
                .setParameter("name", stripped)
                .setParameter("raw", requiredRole)
                .setMaxResults(1)
                .getResultList();
        if (!result.isEmpty()) {
            return result.get(0);
        }
        Role created = new Role();
        created.setRoleName(stripped);
        entityManager.persist(created);
        entityManager.flush();
        return created;
    }

    private String strippedRequiredRole() {
        return requiredRole.startsWith("ROLE_") ? requiredRole.substring(5) : requiredRole;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRealmRoles(Map<String, Object> claims) {
        Object realmAccess = claims.get("realm_access");
        if (realmAccess instanceof Map<?, ?> map) {
            Object roles = map.get("roles");
            if (roles instanceof List<?> list) {
                return (List<String>) list;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Base64URL-decodes the JWT payload. Signature is enforced by the
     * OAuth2 resource server on every protected request, so we only
     * need claims here.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> decodeJwtPayload(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                throw new InvalidCredentialsException("Malformed JWT");
            }
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return objectMapper.readValue(new String(payload, StandardCharsets.UTF_8), Map.class);
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidCredentialsException("Could not parse JWT: " + e.getMessage());
        }
    }

    private LoginResponse buildResponse(User user, KeycloakTokenResponse token, List<String> roles,
                                        LocalDateTime expiresAt, String message) {
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmployeeId(user.getEmployeeId());
        response.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
        response.setLanguage(user.getLanguage());
        response.setAuthToken(token.accessToken());
        response.setAccessToken(token.accessToken());
        response.setTokenType(token.tokenType() != null ? token.tokenType() : "Bearer");
        response.setExpiresIn(token.expiresIn());
        response.setTokenExpiresAt(expiresAt);
        response.setRoles(roles);
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }
}
