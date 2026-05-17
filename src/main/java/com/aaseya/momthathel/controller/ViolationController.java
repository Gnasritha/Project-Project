package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.dto.PreviousViolationDto;
import com.aaseya.momthathel.dto.ViolationCreateRequest;
import com.aaseya.momthathel.dto.ViolationResponse;
import com.aaseya.momthathel.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Previous-violations lookup (ISM-7) + add-violation actions (ISM-8).
 */
@CrossOrigin("*")
@RestController
public class ViolationController {

    @Autowired
    private ViolationService violationService;

    // ── ISM-7 ────────────────────────────────────────────────────────────
    @GetMapping("/api/mobile/licenses/{licenseNumber}/previous-violations")
    public ResponseEntity<?> previousViolations(
            @PathVariable String licenseNumber,
            @RequestParam(required = false) Long excludeInspectionId) {
        try {
            List<PreviousViolationDto> result =
                    violationService.previousViolationsForLicense(licenseNumber, excludeInspectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ── ISM-8 ────────────────────────────────────────────────────────────
    @PostMapping("/api/mobile/visits/{inspectionId}/violations")
    public ResponseEntity<?> addViolation(@PathVariable Long inspectionId,
                                          @RequestBody ViolationCreateRequest request) {
        try {
            ViolationResponse result = violationService.addViolation(inspectionId, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/mobile/visits/{inspectionId}/violations")
    public ResponseEntity<?> listViolations(@PathVariable Long inspectionId) {
        try {
            List<ViolationResponse> result = violationService.listForInspection(inspectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/api/mobile/visits/{inspectionId}/violations/{violationId}")
    public ResponseEntity<?> deleteViolation(@PathVariable Long inspectionId,
                                             @PathVariable Long violationId) {
        try {
            violationService.deleteViolation(inspectionId, violationId);
            Map<String, Object> ok = new HashMap<>();
            ok.put("status", "success");
            ok.put("message", "Violation deleted");
            return ResponseEntity.ok(ok);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
