package com.aaseya.momthathel.keycloak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security + Keycloak (OAuth2 Resource Server) wiring for Momthathel.
 * <p>Mirrors the Logistic-Management-System (LMS) pattern:
 * <ul>
 *   <li>Bearer JWT validated against {@code spring.security.oauth2.resourceserver.jwt.issuer-uri}</li>
 *   <li>Realm roles ({@code realm_access.roles}) + client roles
 *       ({@code resource_access.<keycloak.client-id>.roles}) mapped to
 *       Spring {@code ROLE_*} authorities</li>
 *   <li>Stateless: no sessions, CSRF disabled, permissive CORS</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    @Value("${keycloak.client.id}")
    private String keycloakClientId;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Environment environment) throws Exception {

        // In the `local` dev profile Keycloak is typically not running, so the
        // mobile inspection APIs are opened up and JWT validation is skipped —
        // the app can then be exercised end-to-end without an identity server.
        // Every other profile keeps `/api/mobile/**` protected by a Keycloak JWT.
        boolean localDev = environment.acceptsProfiles(Profiles.of("local"));

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> {
                auth
                    // ── Always-public infrastructure endpoints ──────────────
                    .requestMatchers(
                            "/actuator/health",
                            "/actuator/info",
                            "/actuator/prometheus",
                            "/v3/api-docs/**",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/public/**"
                    ).permitAll()

                    // ── ISM-2 mobile token-login flow lives outside Keycloak
                    //    so it can be used "when standard stage enrolment
                    //    credentials are unavailable or invalid" — these
                    //    endpoints intentionally stay public.
                    .requestMatchers("/api/auth/**").permitAll();

                // ── Mobile-app inspection APIs — Keycloak-JWT protected,
                //    except in `local` dev where they are opened up.
                if (localDev) {
                    auth.requestMatchers("/api/mobile/**").permitAll();
                } else {
                    auth.requestMatchers("/api/mobile/**").authenticated();
                }

                auth.anyRequest().permitAll();
            });

        // Bearer-JWT validation is wired only outside `local` — in `local`
        // it would otherwise reject the app's stub token before the
        // permitAll rule above can apply.
        if (!localDev) {
            http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        }

        return http.build();
    }

    /**
     * Builds Spring {@code GrantedAuthority}s from a Keycloak JWT.
     * Reads BOTH realm-level roles (realm_access.roles) and client-level
     * roles (resource_access.&lt;clientId&gt;.roles), prefixing each with
     * {@code ROLE_} so {@code @PreAuthorize("hasRole('INSPECTOR')")} works.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Realm-level roles (realm_access.roles)
            @SuppressWarnings("unchecked")
            Map<String, Object> realmAccess =
                    (Map<String, Object>) jwt.getClaims().get("realm_access");

            if (realmAccess != null) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                if (roles != null) {
                    roles.forEach(role ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                }
            }

            // Client-level roles (resource_access.<clientId>.roles)
            @SuppressWarnings("unchecked")
            Map<String, Object> resourceAccess =
                    (Map<String, Object>) jwt.getClaims().get("resource_access");

            if (resourceAccess != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> clientAccess =
                        (Map<String, Object>) resourceAccess.get(keycloakClientId);
                if (clientAccess != null) {
                    @SuppressWarnings("unchecked")
                    List<String> clientRoles = (List<String>) clientAccess.get("roles");
                    if (clientRoles != null) {
                        clientRoles.forEach(role ->
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    }
                }
            }

            return authorities;
        });

        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
