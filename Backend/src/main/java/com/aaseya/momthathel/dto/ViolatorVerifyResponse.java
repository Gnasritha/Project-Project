package com.aaseya.momthathel.dto;

/**
 * Response from {@code /api/mobile/violators/verify-individual} or
 * {@code /verify-entity}. Always HTTP 200 — the {@code verified} flag and
 * {@code message} carry the outcome so the mobile app can render either
 * the "Verified" banner or the red invalid-information banner from the
 * same call.
 */
public class ViolatorVerifyResponse {

    private boolean verified;

    /** Full Arabic name once verified (or null on failure). */
    private String name;

    /** Echo of the verified national ID — populated only for Individual. */
    private String idNumber;

    /** Echo of the verified facility number — populated only for Entity. */
    private String nationalFacilityNumber;

    /** Best-effort mobile number from the registry — may be null. */
    private String mobileNumber;

    /** Human-readable explanation when verified=false. */
    private String message;

    public static ViolatorVerifyResponse verifiedIndividual(String name, String idNumber, String mobileNumber) {
        ViolatorVerifyResponse r = new ViolatorVerifyResponse();
        r.verified = true;
        r.name = name;
        r.idNumber = idNumber;
        r.mobileNumber = mobileNumber;
        return r;
    }

    public static ViolatorVerifyResponse verifiedEntity(String name, String facilityNumber, String mobileNumber) {
        ViolatorVerifyResponse r = new ViolatorVerifyResponse();
        r.verified = true;
        r.name = name;
        r.nationalFacilityNumber = facilityNumber;
        r.mobileNumber = mobileNumber;
        return r;
    }

    public static ViolatorVerifyResponse failed(String message) {
        ViolatorVerifyResponse r = new ViolatorVerifyResponse();
        r.verified = false;
        r.message = message;
        return r;
    }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getNationalFacilityNumber() { return nationalFacilityNumber; }
    public void setNationalFacilityNumber(String nationalFacilityNumber) {
        this.nationalFacilityNumber = nationalFacilityNumber;
    }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
