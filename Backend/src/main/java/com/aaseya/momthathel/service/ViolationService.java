package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dao.ClauseRepository;
import com.aaseya.momthathel.dao.InspectionCaseRepository;
import com.aaseya.momthathel.dao.InspectionViolationRepository;
import com.aaseya.momthathel.dto.ClauseResponse;
import com.aaseya.momthathel.dto.PreviousViolationDto;
import com.aaseya.momthathel.dto.ViolationCreateRequest;
import com.aaseya.momthathel.dto.ViolationResponse;
import com.aaseya.momthathel.model.Clause;
import com.aaseya.momthathel.model.InspectionCase;
import com.aaseya.momthathel.model.InspectionViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ViolationService {

    @Autowired
    private ClauseRepository clauseRepository;

    @Autowired
    private InspectionViolationRepository violationRepository;

    @Autowired
    private InspectionCaseRepository caseRepository;

    public List<ClauseResponse> listClauses() {
        try {
            List<Clause> rows = clauseRepository.findAllOrderByClauseCode();
            List<ClauseResponse> out = new ArrayList<>();
            for (Clause c : rows) {
                out.add(new ClauseResponse(c.getClauseId(), c.getClauseCode(), c.getClauseName(), c.getSeverityLevel()));
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<PreviousViolationDto> previousViolationsForLicense(String licenseNumber, Long excludeInspectionId) {
        try {
            if (licenseNumber == null || licenseNumber.isBlank()) {
                throw new RuntimeException("licenseNumber is required");
            }
            return violationRepository.findPreviousViolationsByLicense(licenseNumber, excludeInspectionId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public ViolationResponse addViolation(Long inspectionId, ViolationCreateRequest request) {

        ViolationResponse response = new ViolationResponse();

        try {
            if (inspectionId == null) {
                throw new RuntimeException("inspectionId is required");
            }
            if (request.getClauseId() == null) {
                throw new RuntimeException("clauseId is required");
            }
            if (request.getSeverity() == null || request.getSeverity().isBlank()) {
                throw new RuntimeException("severity is required");
            }

            InspectionCase inspection = caseRepository.findById(inspectionId);
            if (inspection == null) {
                throw new RuntimeException("Inspection not found: " + inspectionId);
            }

            Clause clause = clauseRepository.findById(request.getClauseId());
            if (clause == null) {
                throw new RuntimeException("Clause not found: " + request.getClauseId());
            }

            InspectionViolation violation = new InspectionViolation();
            violation.setInspectionCase(inspection);
            violation.setClause(clause);
            violation.setSeverity(request.getSeverity());
            violation.setViolationDescription(request.getViolationDescription());
            violation.setCorrectiveAction(request.getCorrectiveAction());
            // Non-compliance bottom-sheet fields (all optional)
            violation.setReasonCode(request.getReasonCode());
            violation.setOtherReason(request.getOtherReason());
            violation.setNumberOfUnits(request.getNumberOfUnits());
            violation.setOffenderType(request.getOffenderType());
            violation.setPenaltyCodes(request.getPenaltyCodes());
            violation.setConfiscatedProducts(request.getConfiscatedProducts());
            violation.setInspectorNotes(request.getInspectorNotes());

            InspectionViolation saved = violationRepository.save(violation);

            response.setViolationId(saved.getViolationId());
            response.setInspectionId(inspectionId);
            response.setClauseId(clause.getClauseId());
            response.setClauseCode(clause.getClauseCode());
            response.setClauseName(clause.getClauseName());
            response.setSeverity(saved.getSeverity());
            response.setViolationDescription(saved.getViolationDescription());
            response.setCorrectiveAction(saved.getCorrectiveAction());
            response.setReasonCode(saved.getReasonCode());
            response.setOtherReason(saved.getOtherReason());
            response.setNumberOfUnits(saved.getNumberOfUnits());
            response.setOffenderType(saved.getOffenderType());
            response.setPenaltyCodes(saved.getPenaltyCodes());
            response.setConfiscatedProducts(saved.getConfiscatedProducts());
            response.setInspectorNotes(saved.getInspectorNotes());
            response.setSuccess(true);
            response.setMessage("Violation added successfully");

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ViolationResponse> listForInspection(Long inspectionId) {
        try {
            if (inspectionId == null) {
                throw new RuntimeException("inspectionId is required");
            }
            if (!caseRepository.existsById(inspectionId)) {
                throw new RuntimeException("Inspection not found: " + inspectionId);
            }

            List<InspectionViolation> rows = violationRepository.findByInspectionId(inspectionId);
            List<ViolationResponse> out = new ArrayList<>();
            for (InspectionViolation v : rows) {
                ViolationResponse r = new ViolationResponse();
                r.setViolationId(v.getViolationId());
                r.setInspectionId(inspectionId);
                if (v.getClause() != null) {
                    r.setClauseId(v.getClause().getClauseId());
                    r.setClauseCode(v.getClause().getClauseCode());
                    r.setClauseName(v.getClause().getClauseName());
                }
                r.setSeverity(v.getSeverity());
                r.setViolationDescription(v.getViolationDescription());
                r.setCorrectiveAction(v.getCorrectiveAction());
                r.setReasonCode(v.getReasonCode());
                r.setOtherReason(v.getOtherReason());
                r.setNumberOfUnits(v.getNumberOfUnits());
                r.setOffenderType(v.getOffenderType());
                r.setPenaltyCodes(v.getPenaltyCodes());
                r.setConfiscatedProducts(v.getConfiscatedProducts());
                r.setInspectorNotes(v.getInspectorNotes());
                out.add(r);
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void deleteViolation(Long inspectionId, Long violationId) {
        try {
            InspectionViolation v = violationRepository.findById(violationId);
            if (v == null) {
                throw new RuntimeException("Violation not found: " + violationId);
            }
            if (v.getInspectionCase() == null || !inspectionId.equals(v.getInspectionCase().getInspectionId())) {
                throw new RuntimeException(
                        "Violation " + violationId + " does not belong to inspection " + inspectionId);
            }
            violationRepository.delete(v);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
