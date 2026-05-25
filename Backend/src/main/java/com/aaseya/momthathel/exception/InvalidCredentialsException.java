package com.aaseya.momthathel.exception;

/**
 * Thrown when Keycloak rejects the supplied username/password (HTTP 4xx
 * from the token endpoint) or returns an empty/malformed token response.
 * Mapped to HTTP 401 by {@link GlobalExceptionHandler}.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
