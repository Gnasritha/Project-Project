package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.dto.ViolatorVerifyEntityRequest;
import com.aaseya.momthathel.dto.ViolatorVerifyIndividualRequest;
import com.aaseya.momthathel.dto.ViolatorVerifyResponse;
import com.aaseya.momthathel.service.ViolatorVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Mobile-app endpoints behind the "Fill in violator data" bottom sheet
 * (Sprint 2 screenshots 7-11). Always returns HTTP 200 — the
 * {@code verified} field on the response body carries success/failure so
 * the bottom sheet can render its banners without distinguishing API
 * errors from validation failures.
 */
@RestController
@RequestMapping("/api/mobile/violators")
public class ViolatorController {

    @Autowired
    private ViolatorVerificationService verificationService;

    /**
     * Verifies a natural-person violator by Saudi National ID / Iqama
     * plus Gregorian birth date.
     */
    @PostMapping("/verify-individual")
    public ResponseEntity<ViolatorVerifyResponse> verifyIndividual(
            @RequestBody ViolatorVerifyIndividualRequest request) {
        return ResponseEntity.ok(verificationService.verifyIndividual(request));
    }

    /**
     * Verifies an establishment / entity violator by national facility
     * number (commercial-registry id).
     */
    @PostMapping("/verify-entity")
    public ResponseEntity<ViolatorVerifyResponse> verifyEntity(
            @RequestBody ViolatorVerifyEntityRequest request) {
        return ResponseEntity.ok(verificationService.verifyEntity(request));
    }
}
