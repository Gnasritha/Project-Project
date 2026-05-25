/**
 * Keycloak / Spring Security wiring for Momthathel.
 *
 * <p>Pattern mirrors the Logistic-Management-System (LMS) backend:
 * <ul>
 *   <li>{@code spring-boot-starter-oauth2-resource-server} validates Bearer
 *       JWTs against {@code spring.security.oauth2.resourceserver.jwt.issuer-uri}
 *       (the Keycloak realm URL).</li>
 *   <li>{@link com.aaseya.momthathel.keycloak.SecurityConfig} declares the
 *       filter chain, the JWT → role converter, and the CORS policy.</li>
 *   <li>{@code keycloak.client-id} (in {@code application.yml}) names the
 *       client whose roles are read from {@code resource_access.<id>.roles}.</li>
 * </ul>
 */
package com.aaseya.momthathel.keycloak;
