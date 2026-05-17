package com.aaseya.momthathel.dto;

import java.time.LocalDateTime;

public class VisitCreateRequest {

    private String inspectionNumber;
    private Long inspectionTypeId;
    private Long establishmentId;
    private Long licenseId;
    private boolean licenseAvailable;
    private String licenseEntryMethod;
    private String inspectionMode;
    private LocalDateTime inspectionDate;
    private Double latitude;
    private Double longitude;
    private Boolean familyRelationshipDisclosed;

    public String getInspectionNumber() { return inspectionNumber; }
    public void setInspectionNumber(String inspectionNumber) { this.inspectionNumber = inspectionNumber; }

    public Long getInspectionTypeId() { return inspectionTypeId; }
    public void setInspectionTypeId(Long inspectionTypeId) { this.inspectionTypeId = inspectionTypeId; }

    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }

    public Long getLicenseId() { return licenseId; }
    public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }

    public boolean isLicenseAvailable() { return licenseAvailable; }
    public void setLicenseAvailable(boolean licenseAvailable) { this.licenseAvailable = licenseAvailable; }

    public String getLicenseEntryMethod() { return licenseEntryMethod; }
    public void setLicenseEntryMethod(String licenseEntryMethod) { this.licenseEntryMethod = licenseEntryMethod; }

    public String getInspectionMode() { return inspectionMode; }
    public void setInspectionMode(String inspectionMode) { this.inspectionMode = inspectionMode; }

    public LocalDateTime getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDateTime inspectionDate) { this.inspectionDate = inspectionDate; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getFamilyRelationshipDisclosed() { return familyRelationshipDisclosed; }
    public void setFamilyRelationshipDisclosed(Boolean familyRelationshipDisclosed) { this.familyRelationshipDisclosed = familyRelationshipDisclosed; }
}
