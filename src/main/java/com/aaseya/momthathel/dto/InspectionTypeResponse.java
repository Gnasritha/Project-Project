package com.aaseya.momthathel.dto;

public class InspectionTypeResponse {

    private Long inspectionTypeId;
    private String inspectionCode;
    private String inspectionName;
    private boolean active;

    public InspectionTypeResponse() {}

    public InspectionTypeResponse(Long inspectionTypeId, String inspectionCode, String inspectionName, boolean active) {
        this.inspectionTypeId = inspectionTypeId;
        this.inspectionCode = inspectionCode;
        this.inspectionName = inspectionName;
        this.active = active;
    }

    public Long getInspectionTypeId() { return inspectionTypeId; }
    public void setInspectionTypeId(Long inspectionTypeId) { this.inspectionTypeId = inspectionTypeId; }

    public String getInspectionCode() { return inspectionCode; }
    public void setInspectionCode(String inspectionCode) { this.inspectionCode = inspectionCode; }

    public String getInspectionName() { return inspectionName; }
    public void setInspectionName(String inspectionName) { this.inspectionName = inspectionName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
