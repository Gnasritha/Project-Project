package com.aaseya.momthathel.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Maps to the {@code LICENSE} table.
 */
@Entity
@Table(name = "license",
       uniqueConstraints = @UniqueConstraint(name = "uk_license_number", columnNames = "license_number"))
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_seq")
    @SequenceGenerator(name = "license_seq", sequenceName = "license_license_id_seq", allocationSize = 1)
    @Column(name = "license_id")
    private Long licenseId;

    @Column(name = "license_number", nullable = false, unique = true, length = 100)
    private String licenseNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "establishment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_license_establishment"))
    private Establishment establishment;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "license_status", length = 50)
    private String licenseStatus;

    @Column(name = "license_type", length = 100)
    private String licenseType;

    public License() {}

    public Long getLicenseId() { return licenseId; }
    public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Establishment getEstablishment() { return establishment; }
    public void setEstablishment(Establishment establishment) { this.establishment = establishment; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getLicenseStatus() { return licenseStatus; }
    public void setLicenseStatus(String licenseStatus) { this.licenseStatus = licenseStatus; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }
}
