package com.aaseya.momthathel.dto;

/**
 * Request body for {@code POST /api/mobile/violators/verify-individual} —
 * the "Fill in violator data" bottom sheet, Individual tab. The combination
 * of {@code idNumber} (10-digit Saudi National ID / Iqama) and the
 * Gregorian birth date is what a real Yakeen / Absher integration would
 * validate against. For local dev the service stubs basic format checks.
 */
public class ViolatorVerifyIndividualRequest {

    /** 10-digit Saudi national ID / Iqama number. */
    private String idNumber;

    /** Gregorian birth date in {@code yyyy-MM-dd} or {@code yyyy/M/d}. */
    private String birthDate;

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
}
