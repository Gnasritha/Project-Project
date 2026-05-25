package com.aaseya.momthathel.dto;

public class FacilityCreateRequest {

    private String facilityName;
    private String location;
    private String city;
    private Double latitude;
    private Double longitude;
    private Long isicId;
    private Long isicDetailId;

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

    public Long getIsicId() { return isicId; }
    public void setIsicId(Long isicId) { this.isicId = isicId; }

    public Long getIsicDetailId() { return isicDetailId; }
    public void setIsicDetailId(Long isicDetailId) { this.isicDetailId = isicDetailId; }
}
