package com.aaseya.momthathel.dto;

public class NoLicenseVisitRequest {

    private String inspectionNumber;
    private Long inspectionTypeId;
    private String inspectionMode;
    private FacilityCreateRequest facility;

    public String getInspectionNumber() { return inspectionNumber; }
    public void setInspectionNumber(String inspectionNumber) { this.inspectionNumber = inspectionNumber; }

    public Long getInspectionTypeId() { return inspectionTypeId; }
    public void setInspectionTypeId(Long inspectionTypeId) { this.inspectionTypeId = inspectionTypeId; }

    public String getInspectionMode() { return inspectionMode; }
    public void setInspectionMode(String inspectionMode) { this.inspectionMode = inspectionMode; }

    public FacilityCreateRequest getFacility() { return facility; }
    public void setFacility(FacilityCreateRequest facility) { this.facility = facility; }
}
