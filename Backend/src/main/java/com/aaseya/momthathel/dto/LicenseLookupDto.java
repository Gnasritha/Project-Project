package com.aaseya.momthathel.dto;

import java.time.LocalDate;

public class LicenseLookupDto {

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
    private String mobileNumber;
    private String nationalFacilityNumber;

    public LicenseLookupDto() {}

    public LicenseLookupDto(Long licenseId, String licenseNumber, String licenseStatus, String licenseType,
                            LocalDate issueDate, LocalDate expiryDate, Long establishmentId, String establishmentName,
                            String activityType, String address, String city, String mobileNumber,
                            String nationalFacilityNumber) {
        this.licenseId = licenseId;
        this.licenseNumber = licenseNumber;
        this.licenseStatus = licenseStatus;
        this.licenseType = licenseType;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.establishmentId = establishmentId;
        this.establishmentName = establishmentName;
        this.activityType = activityType;
        this.address = address;
        this.city = city;
        this.mobileNumber = mobileNumber;
        this.nationalFacilityNumber = nationalFacilityNumber;
    }

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

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getNationalFacilityNumber() { return nationalFacilityNumber; }
    public void setNationalFacilityNumber(String nationalFacilityNumber) { this.nationalFacilityNumber = nationalFacilityNumber; }
}
