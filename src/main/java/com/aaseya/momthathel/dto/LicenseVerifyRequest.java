package com.aaseya.momthathel.dto;

public class LicenseVerifyRequest {

    private String licenseNumber;
    private String entryMethod;

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getEntryMethod() { return entryMethod; }
    public void setEntryMethod(String entryMethod) { this.entryMethod = entryMethod; }
}
