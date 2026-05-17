package com.aaseya.momthathel.service;


import com.aaseya.momthathel.dto.LoginRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KeycloakUserService {

    @Value("${keycloak.server-url:http://10.13.1.180:8180}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm:InspectionManagement}")
    private String realm;

    @Value("${keycloak.client.id:inspection-mobile-app}")
    private String clientId;

    private final RestTemplate restTemplate =
            new RestTemplate();

    /**
     * INSPECTOR LOGIN
     * USED FOR FLUTTER MOBILE APP
     */

    public Map<String, Object> inspectorLogin(
    		LoginRequest loginDTO) {

        try {

            /**
             * KEYCLOAK TOKEN URL
             */

            String tokenUrl =
                    keycloakServerUrl
                            + "/realms/"
                            + realm
                            + "/protocol/openid-connect/token";

            /**
             * HEADERS
             */

            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_FORM_URLENCODED
            );

            /**
             * REQUEST BODY
             */

            MultiValueMap<String, String> body =
                    new LinkedMultiValueMap<>();

            body.add(
                    "client_id",
                    clientId
            );

            body.add(
                    "grant_type",
                    "password"
            );

            body.add(
                    "username",
                    loginDTO.getUsername()
            );

            body.add(
                    "password",
                    loginDTO.getPassword()
            );

            /**
             * REQUEST ENTITY
             */

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(
                            body,
                            headers
                    );

            /**
             * CALL KEYCLOAK API
             */

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            tokenUrl,
                            request,
                            Map.class
                    );

            /**
             * SUCCESS RESPONSE
             */

            if (response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null) {

                Map<String, Object> result =
                        new HashMap<>();

                result.put(
                        "access_token",
                        response.getBody()
                                .get("access_token")
                );

                result.put(
                        "refresh_token",
                        response.getBody()
                                .get("refresh_token")
                );

                result.put(
                        "expires_in",
                        response.getBody()
                                .get("expires_in")
                );

                result.put(
                        "token_type",
                        response.getBody()
                                .get("token_type")
                );

                result.put(
                        "message",
                        "Inspector Login Successful"
                );

                return result;
            }

            throw new RuntimeException(
                    "Invalid Username Or Password"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Inspector Login Failed : "
                            + e.getMessage()
            );
        }
    }
}