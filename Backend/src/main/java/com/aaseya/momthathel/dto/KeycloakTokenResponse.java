package com.aaseya.momthathel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wire shape returned by Keycloak's
 * {@code /realms/{realm}/protocol/openid-connect/token} endpoint
 * for a successful {@code grant_type=password} call.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakTokenResponse(
        @JsonProperty("access_token")  String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in")    long expiresIn,
        @JsonProperty("token_type")    String tokenType) {
}
