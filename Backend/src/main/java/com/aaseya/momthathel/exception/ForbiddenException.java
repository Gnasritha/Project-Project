package com.aaseya.momthathel.exception;

/**
 * Thrown when the caller authenticated successfully but lacks the
 * required role (e.g. an authenticated Keycloak user without the
 * {@code INSPECTOR} realm role). Mapped to HTTP 403 by
 * {@link GlobalExceptionHandler}.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
