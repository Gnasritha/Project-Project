package com.aaseya.momthathel.dto;

public class FacilityResponse {

    private Long establishmentId;
    private String facilityName;
    private String location;
    private String city;
    private Double latitude;
    private Double longitude;
    private String isicName;
    private String detailActivityName;
    private boolean licenseAvailable;

    private boolean success;
    private String message;

    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getIsicName() { return isicName; }
    public void setIsicName(String isicName) { this.isicName = isicName; }

    public String getDetailActivityName() { return detailActivityName; }
    public void setDetailActivityName(String detailActivityName) { this.detailActivityName = detailActivityName; }

    public boolean isLicenseAvailable() { return licenseAvailable; }
    public void setLicenseAvailable(boolean licenseAvailable) { this.licenseAvailable = licenseAvailable; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
