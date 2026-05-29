package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.dto.FacilityCreateRequest;
import com.aaseya.momthathel.dto.FacilityResponse;
import com.aaseya.momthathel.dto.InspectionTypeResponse;
import com.aaseya.momthathel.dto.LicenseVerifyRequest;
import com.aaseya.momthathel.dto.LicenseVerifyResponse;
import com.aaseya.momthathel.dto.NoLicenseVisitRequest;
import com.aaseya.momthathel.dto.VisitCreateRequest;
import com.aaseya.momthathel.dto.VisitResponse;
import com.aaseya.momthathel.service.InspectorVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mobile Inspection Visit endpoints (ISM-3, ISM-4, ISM-5).
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/mobile/visits")
public class MobileVisitController {

    @Autowired
    private InspectorVisitService visitService;

    @GetMapping("/inspection-types")
    public ResponseEntity<?> getMappedInspectionTypes(@RequestParam("inspectorId") Long inspectorId) {
        try {
            List<InspectionTypeResponse> result = visitService.getMappedInspectionTypes(inspectorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/verify-license")
    public ResponseEntity<?> verifyLicense(@RequestBody LicenseVerifyRequest request) {
        try {
            LicenseVerifyResponse result = visitService.verifyLicense(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /** Mobile API #1 — Case creation. Also starts the PerformInspection BPMN process. */
    @PostMapping
    public ResponseEntity<?> createVisit(@RequestBody VisitCreateRequest request,
                                         @RequestParam("inspectorId") Long inspectorId) {
        try {
            VisitResponse result = visitService.createVisit(request, inspectorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /** Mobile API #2 — Fetch a single case (with its BPMN process keys). */
    @GetMapping("/{inspectionId}")
    public ResponseEntity<?> getVisit(@PathVariable("inspectionId") Long inspectionId) {
        try {
            VisitResponse result = visitService.getVisit(inspectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /** Mobile API #2 (list) — All cases raised by an inspector, newest first. */
    @GetMapping
    public ResponseEntity<?> getVisits(@RequestParam("inspectorId") Long inspectorId) {
        try {
            List<VisitResponse> result = visitService.getVisitsByInspector(inspectorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /** Mobile API #3 — Submit a case; marks it SUBMITTED and signals the BPMN process. */
    @PostMapping("/{inspectionId}/submit")
    public ResponseEntity<?> submitVisit(@PathVariable("inspectionId") Long inspectionId) {
        try {
            VisitResponse result = visitService.submitVisit(inspectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/facility")
    public ResponseEntity<?> createFacility(@RequestBody FacilityCreateRequest request) {
        try {
            FacilityResponse result = visitService.createFacility(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/no-license")
    public ResponseEntity<?> createNoLicenseVisit(@RequestBody NoLicenseVisitRequest request,
                                                  @RequestParam("inspectorId") Long inspectorId) {
        try {
            VisitResponse result = visitService.createNoLicenseVisit(request, inspectorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
