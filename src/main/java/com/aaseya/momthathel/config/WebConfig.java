package com.aaseya.momthathel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration.
 *
 * <p>Active in EVERY profile (including {@code local}, where the
 * {@link com.aaseya.momthathel.keycloak.SecurityConfig} is disabled and
 * its CORS bean is therefore not loaded).</p>
 *
 * <p>Permits requests from any origin so the Flutter mobile/web app can
 * call {@code /api/**} during development. Tighten {@code allowedOriginPatterns}
 * for QA / prod by overriding this bean.</p>
 */
@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Content-Disposition")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}
