/**
 * Exception handling.
 *
 * <ul>
 *   <li>Custom checked/unchecked exception classes per domain</li>
 *   <li>{@code GlobalExceptionHandler} — @RestControllerAdvice that maps
 *       exceptions to HTTP status codes and {@code ApiResponse} error payloads</li>
 * </ul>
 *
 * <p>Camunda client exceptions (e.g. {@code CamundaClientException}) should
 * also be caught here and surfaced as meaningful HTTP responses.</p>
 */
package com.aaseya.momthathel.exception;
