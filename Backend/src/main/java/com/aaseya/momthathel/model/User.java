package com.aaseya.momthathel.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Maps to the {@code USERS} table.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role;

    @Column(name = "employee_id", length = 100)
    private String employeeId;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", length = 512)
    private String passwordHash;

    @Column(name = "keycloak_sub", length = 64, unique = true)
    private String keycloakSub;

    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "auth_token", length = 512, unique = true)
    private String authToken;

    @Column(name = "token_generated_at")
    private LocalDateTime tokenGeneratedAt;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_inspection_type",
        joinColumns        = @JoinColumn(name = "user_id",           referencedColumnName = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "inspection_type_id", referencedColumnName = "inspection_type_id")
    )
    private Set<InspectionType> authorisedInspectionTypes = new HashSet<>();

    @OneToMany(mappedBy = "inspector", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InspectionCase> inspectionCases = new ArrayList<>();

    public User() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getKeycloakSub() { return keycloakSub; }
    public void setKeycloakSub(String keycloakSub) { this.keycloakSub = keycloakSub; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    public LocalDateTime getTokenGeneratedAt() { return tokenGeneratedAt; }
    public void setTokenGeneratedAt(LocalDateTime tokenGeneratedAt) { this.tokenGeneratedAt = tokenGeneratedAt; }

    public LocalDateTime getTokenExpiresAt() { return tokenExpiresAt; }
    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) { this.tokenExpiresAt = tokenExpiresAt; }

    public Set<InspectionType> getAuthorisedInspectionTypes() { return authorisedInspectionTypes; }
    public void setAuthorisedInspectionTypes(Set<InspectionType> authorisedInspectionTypes) { this.authorisedInspectionTypes = authorisedInspectionTypes; }

    public List<InspectionCase> getInspectionCases() { return inspectionCases; }
    public void setInspectionCases(List<InspectionCase> inspectionCases) { this.inspectionCases = inspectionCases; }
}
