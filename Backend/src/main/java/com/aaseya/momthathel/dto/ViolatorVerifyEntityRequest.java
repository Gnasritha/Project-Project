package com.aaseya.momthathel.dto;

/**
 * Request body for {@code POST /api/mobile/violators/verify-entity} —
 * the "Fill in violator data" bottom sheet, Entity tab. A real
 * implementation would call the commercial registry (Wathiq / MCI) with
 * the national facility number; the local-dev stub validates format only.
 */
public class ViolatorVerifyEntityRequest {

    /** 7+ digit Saudi national facility / commercial registry number. */
    private String nationalFacilityNumber;

    public String getNationalFacilityNumber() { return nationalFacilityNumber; }
    public void setNationalFacilityNumber(String nationalFacilityNumber) {
        this.nationalFacilityNumber = nationalFacilityNumber;
    }
}
