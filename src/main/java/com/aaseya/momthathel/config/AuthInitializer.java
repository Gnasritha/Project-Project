package com.aaseya.momthathel.config;

import com.aaseya.momthathel.dao.UserRepository;
import com.aaseya.momthathel.model.User;
import com.aaseya.momthathel.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds proper bcrypt password hashes for users that were inserted via
 * {@code seed-data.sql} with a placeholder hash like
 * {@code $2a$10$dummyhashplaceholder}.
 *
 * <p>Runs once at app startup. After execution {@code inspector01} can log
 * in via {@code POST /api/auth/login} with password {@code Password@123}.</p>
 */
@Component
public class AuthInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AuthInitializer.class);

    private static final String[] PLACEHOLDER_PREFIXES = {
            "$2a$10$dummy",                  // postman/seed-data.sql
            "$2a$10$abcdefghijklmnopqrstuv"  // 02_data.sql
    };
    private static final String DEFAULT_PASSWORD = "Password@123";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Override
    public void run(String... args) {
        for (String prefix : PLACEHOLDER_PREFIXES) {
            try {
                for (User u : userRepository.findAllWithDummyPassword(prefix)) {
                    String newHash = authService.hashPassword(DEFAULT_PASSWORD);
                    u.setPasswordHash(newHash);
                    userRepository.saveOrUpdate(u);
                    log.info("[AuthInitializer] Reset password for user '{}' → default '{}'",
                            u.getUsername(), DEFAULT_PASSWORD);
                }
            } catch (Exception e) {
                log.warn("[AuthInitializer] Failed to seed default passwords (prefix {}): {}", prefix, e.getMessage());
            }
        }
    }
}
