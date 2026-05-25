package com.aaseya.momthathel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

/**
 * Mobile-app login payload. The {@code username} field carries whatever
 * the inspector typed into the "ID Number / Mobile" box on the login
 * screen — Keycloak resolves it against {@code username} or any attribute
 * mapped to it (national-id, mobile, etc.).
 *
 * <p>{@code language} and {@code rememberMe} are optional client hints
 * — accepted for forward-compat but not currently used server-side.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginRequest(
        @NotBlank(message = "username is required") String username,
        @NotBlank(message = "password is required") String password,
        String language,
        Boolean rememberMe) {
}
