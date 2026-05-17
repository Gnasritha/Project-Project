/**
 * JPA Entity classes — mapped to PostgreSQL {@code momthathel} tables.
 *
 * <p>Conventions:</p>
 * <ul>
 *   <li>All entities annotated with {@code @Entity} and {@code @Table(name="...")}</li>
 *   <li>Primary key uses {@code @GeneratedValue(strategy = GenerationType.SEQUENCE)}
 *       for PostgreSQL compatibility</li>
 *   <li>Auditing columns (created_at, updated_at) use
 *       {@code @CreationTimestamp} / {@code @UpdateTimestamp}</li>
 *   <li>Use Lombok {@code @Data} / {@code @Builder} to reduce boilerplate</li>
 * </ul>
 *
 * <p>Entities must NOT contain DTO or controller logic.</p>
 */
package com.aaseya.momthathel.model;
