package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.BaseIntegrationTest;
import com.aaseya.momthathel.dto.ViolationCreateRequest;
import com.aaseya.momthathel.model.Clause;
import com.aaseya.momthathel.model.Establishment;
import com.aaseya.momthathel.model.InspectionCase;
import com.aaseya.momthathel.model.InspectionType;
import com.aaseya.momthathel.model.License;
import com.aaseya.momthathel.model.Role;
import com.aaseya.momthathel.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ViolationControllerTest extends BaseIntegrationTest {

    private record Fixture(InspectionCase inspectionCase, License license, Clause clause) {}

    private Fixture seed() {
        Role role = testData.createRole("INSP-V-" + System.nanoTime());
        InspectionType type = testData.createInspectionType("HEALTH", "Health & Safety", true);
        Set<InspectionType> auth = new HashSet<>();
        auth.add(type);
        User inspector = testData.createInspector(role, "v-user-" + System.nanoTime(),
                "V User", "EMP-V", auth);
        Establishment estab = testData.createEstablishment("Cafe", "Food", "Riyadh", type);
        License license = testData.createLicense("LIC-V-" + System.nanoTime(), estab, "ACTIVE", "Commercial");
        InspectionCase ic = testData.createInspectionCase("INS-V-" + System.nanoTime(),
                inspector, type, estab, license);
        Clause clause = testData.createClause("C-1.04", "Refrigeration inadequate", "HIGH");
        return new Fixture(ic, license, clause);
    }

    // ─── ISM-7: GET /api/mobile/licenses/{ln}/previous-violations ────────

    @Test
    void previousViolations_returnsHistoricRowsForLicense() throws Exception {
        Fixture f = seed();
        testData.createViolation(f.inspectionCase(), f.clause(), "HIGH", "Chiller too warm");
        testData.createViolation(f.inspectionCase(), f.clause(), "MEDIUM", "Hand-wash sink dirty");

        mockMvc.perform(get("/api/mobile/licenses/" + f.license().getLicenseNumber() + "/previous-violations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void previousViolations_excludesGivenInspectionId() throws Exception {
        Fixture f = seed();
        testData.createViolation(f.inspectionCase(), f.clause(), "HIGH", "Chiller too warm");

        mockMvc.perform(get("/api/mobile/licenses/" + f.license().getLicenseNumber() + "/previous-violations")
                        .param("excludeInspectionId", f.inspectionCase().getInspectionId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── ISM-8: POST /api/mobile/visits/{id}/violations ──────────────────

    @Test
    void addViolation_createsViolationAgainstInspection() throws Exception {
        Fixture f = seed();

        ViolationCreateRequest req = new ViolationCreateRequest();
        req.setClauseId(f.clause().getClauseId());
        req.setSeverity("HIGH");
        req.setViolationDescription("Walk-in chiller > 8°C");
        req.setCorrectiveAction("Repair compressor; revisit in 14 days");

        mockMvc.perform(post("/api/mobile/visits/" + f.inspectionCase().getInspectionId() + "/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationId").isNumber())
                .andExpect(jsonPath("$.clauseCode").value("C-1.04"))
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void addViolation_returns400ForUnknownClause() throws Exception {
        Fixture f = seed();

        ViolationCreateRequest req = new ViolationCreateRequest();
        req.setClauseId(999_999L);
        req.setSeverity("HIGH");

        mockMvc.perform(post("/api/mobile/visits/" + f.inspectionCase().getInspectionId() + "/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Clause not found: 999999"));
    }

    @Test
    void listViolations_returnsViolationsForInspection() throws Exception {
        Fixture f = seed();
        testData.createViolation(f.inspectionCase(), f.clause(), "HIGH", "v1");
        testData.createViolation(f.inspectionCase(), f.clause(), "MEDIUM", "v2");

        mockMvc.perform(get("/api/mobile/visits/" + f.inspectionCase().getInspectionId() + "/violations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteViolation_removesRow() throws Exception {
        Fixture f = seed();
        Long vId = testData.createViolation(f.inspectionCase(), f.clause(), "HIGH", "doomed").getViolationId();

        mockMvc.perform(delete("/api/mobile/visits/" + f.inspectionCase().getInspectionId() + "/violations/" + vId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(delete("/api/mobile/visits/" + f.inspectionCase().getInspectionId() + "/violations/" + vId))
                .andExpect(status().isBadRequest());
    }
}
