package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dto.KeycloakTokenResponse;
import com.aaseya.momthathel.dto.LoginRequest;
import com.aaseya.momthathel.exception.InvalidCredentialsException;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * Thin HTTP client over the Keycloak token endpoint.
 *
 * <p>Performs the single-call ROPC (Resource Owner Password Credentials) flow:
 * POST {server-url}/realms/{realm}/protocol/openid-connect/token with
 * grant_type=password. Returns the parsed {@link KeycloakTokenResponse} to
 * the caller (AuthService), which then handles role validation and local
 * user upsert.</p>
 *
 * <p>Mirrors the LMS-reference shape (RestClient, typed exceptions).</p>
 */
@Service
public class KeycloakUserService {

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret:}")
    private String clientSecret;

    private final RestClient restClient = RestClient.create();

    private String tokenUrl;

    @PostConstruct
    void init() {
        tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    /**
     * Calls Keycloak's token endpoint with the user's credentials.
     *
     * @return the parsed token response from Keycloak
     * @throws InvalidCredentialsException on Keycloak 4xx (bad credentials,
     *                                     disabled user) or empty body
     * @throws RuntimeException            on Keycloak 5xx
     */
    public KeycloakTokenResponse inspectorLogin(LoginRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", request.username());
        form.add("password", request.password());
        if (StringUtils.hasText(clientSecret)) {
            form.add("client_secret", clientSecret);
        }

        KeycloakTokenResponse token = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new InvalidCredentialsException(
                            "Keycloak rejected credentials (HTTP " + res.getStatusCode().value() + ")");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RuntimeException(
                            "Keycloak server error (HTTP " + res.getStatusCode().value() + ")");
                })
                .body(KeycloakTokenResponse.class);

        if (token == null || !StringUtils.hasText(token.accessToken())) {
            throw new InvalidCredentialsException("Empty token response from Keycloak");
        }
        return token;
    }
}
