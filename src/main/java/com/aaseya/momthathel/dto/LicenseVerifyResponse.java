package com.aaseya.momthathel.dto;

import java.time.LocalDate;

public class LicenseVerifyResponse {

    private boolean found;
    private Long licenseId;
    private String licenseNumber;
    private String licenseStatus;
    private String licenseType;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    private Long establishmentId;
    private String establishmentName;
    private String activityType;
    private String address;
    private String city;

    private String message;
    private boolean success;

    public boolean isFound() { return found; }
    public void setFound(boolean found) { this.found = found; }

    public Long getLicenseId() { return licenseId; }
    public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getLicenseStatus() { return licenseStatus; }
    public void setLicenseStatus(String licenseStatus) { this.licenseStatus = licenseStatus; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }

    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
