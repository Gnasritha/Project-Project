/**
 * Data Transfer Objects (DTOs).
 *
 * <p>DTOs decouple the API layer from the JPA model layer.
 * They carry only the fields relevant to a specific request or response.</p>
 *
 * <ul>
 *   <li>Request DTOs   — validated with Jakarta Bean Validation annotations</li>
 *   <li>Response DTOs  — serialised to JSON by Jackson</li>
 *   <li>{@code ApiResponse<T>} — generic envelope for all REST responses</li>
 * </ul>
 *
 * <p>DTOs must NOT contain JPA annotations (@Entity, @Column, etc.).</p>
 */
package com.aaseya.momthathel.dto;
