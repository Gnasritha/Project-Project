package com.aaseya.momthathel.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps to the {@code INSPECTION_CASE} table.
 */
@Entity
@Table(name = "inspection_case",
       uniqueConstraints = @UniqueConstraint(name = "uk_inspection_number", columnNames = "inspection_number"))
public class InspectionCase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inspection_case_seq")
    @SequenceGenerator(name = "inspection_case_seq", sequenceName = "inspection_case_inspection_id_seq", allocationSize = 1)
    @Column(name = "inspection_id")
    private Long inspectionId;

    @Column(name = "inspection_number", nullable = false, unique = true, length = 100)
    private String inspectionNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_case_inspection_type"))
    private InspectionType inspectionType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspector_id", nullable = false, foreignKey = @ForeignKey(name = "fk_case_inspector"))
    private User inspector;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "establishment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_case_establishment"))
    private Establishment establishment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id", foreignKey = @ForeignKey(name = "fk_case_license"))
    private License license;

    @Column(name = "inspection_mode", length = 50)
    private String inspectionMode;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "current_stage", length = 100)
    private String currentStage;

    @Column(name = "inspection_date")
    private LocalDateTime inspectionDate;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "inspectionCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "inspectionCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InspectionViolation> violations = new ArrayList<>();

    public InspectionCase() {}

    public Long getInspectionId() { return inspectionId; }
    public void setInspectionId(Long inspectionId) { this.inspectionId = inspectionId; }

    public String getInspectionNumber() { return inspectionNumber; }
    public void setInspectionNumber(String inspectionNumber) { this.inspectionNumber = inspectionNumber; }

    public InspectionType getInspectionType() { return inspectionType; }
    public void setInspectionType(InspectionType inspectionType) { this.inspectionType = inspectionType; }

    public User getInspector() { return inspector; }
    public void setInspector(User inspector) { this.inspector = inspector; }

    public Establishment getEstablishment() { return establishment; }
    public void setEstablishment(Establishment establishment) { this.establishment = establishment; }

    public License getLicense() { return license; }
    public void setLicense(License license) { this.license = license; }

    public String getInspectionMode() { return inspectionMode; }
    public void setInspectionMode(String inspectionMode) { this.inspectionMode = inspectionMode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }

    public LocalDateTime getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDateTime inspectionDate) { this.inspectionDate = inspectionDate; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }


    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

    public List<InspectionViolation> getViolations() { return violations; }
    public void setViolations(List<InspectionViolation> violations) { this.violations = violations; }
}
