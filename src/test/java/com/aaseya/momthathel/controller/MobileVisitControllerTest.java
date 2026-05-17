package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.BaseIntegrationTest;
import com.aaseya.momthathel.dto.FacilityCreateRequest;
import com.aaseya.momthathel.dto.LicenseVerifyRequest;
import com.aaseya.momthathel.dto.NoLicenseVisitRequest;
import com.aaseya.momthathel.dto.VisitCreateRequest;
import com.aaseya.momthathel.model.Establishment;
import com.aaseya.momthathel.model.InspectionType;
import com.aaseya.momthathel.model.IsicActivity;
import com.aaseya.momthathel.model.License;
import com.aaseya.momthathel.model.Role;
import com.aaseya.momthathel.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MobileVisitControllerTest extends BaseIntegrationTest {

    private record Fixture(User inspector, InspectionType type, Establishment estab, License license) {}

    private Fixture seedAuthorisedInspector() {
        Role role = testData.createRole("INSPECTOR-MV-" + System.nanoTime());
        InspectionType type = testData.createInspectionType("HEALTH", "Health & Safety", true);

        Set<InspectionType> auth = new HashSet<>();
        auth.add(type);
        User inspector = testData.createInspector(role, "mv-user-" + System.nanoTime(),
                "Mobile Visit User", "EMP-MV", auth);

        Establishment estab = testData.createEstablishment("Al-Rashid Trading", "Retail", "Riyadh", type);
        License license = testData.createLicense("LIC-" + System.nanoTime(), estab, "ACTIVE", "Commercial License");

        return new Fixture(inspector, type, estab, license);
    }

    // ─── GET /api/mobile/visits/inspection-types ─────────────────────────

    @Test
    void getInspectionTypes_returnsOnlyAuthorisedActive() throws Exception {
        Fixture f = seedAuthorisedInspector();
        testData.createInspectionType("FIRE", "Fire Safety", true); // not authorised
        testData.createInspectionType("OLD", "Retired Type", false);

        mockMvc.perform(get("/api/mobile/visits/inspection-types").param("inspectorId", f.inspector().getUserId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].inspectionCode").value("HEALTH"));
    }

    @Test
    void getInspectionTypes_returns400WhenInspectorMissing() throws Exception {
        mockMvc.perform(get("/api/mobile/visits/inspection-types").param("inspectorId", "99999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Inspector not found: 99999"));
    }

    // ─── POST /api/mobile/visits/verify-license ──────────────────────────

    @Test
    void verifyLicense_returnsEstablishmentWhenFound() throws Exception {
        Fixture f = seedAuthorisedInspector();
        LicenseVerifyRequest req = new LicenseVerifyRequest();
        req.setLicenseNumber(f.license().getLicenseNumber());

        mockMvc.perform(post("/api/mobile/visits/verify-license")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.licenseStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.establishmentName").value("Al-Rashid Trading"));
    }

    @Test
    void verifyLicense_returnsNotFoundMessageForInvalidLicense() throws Exception {
        LicenseVerifyRequest req = new LicenseVerifyRequest();
        req.setLicenseNumber("DOES-NOT-EXIST");

        mockMvc.perform(post("/api/mobile/visits/verify-license")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(false))
                .andExpect(jsonPath("$.message").value(
                        "License number not found in the registry. Please check and try again."));
    }

    // ─── POST /api/mobile/visits ─────────────────────────────────────────

    @Test
    void createVisit_succeedsForAuthorisedInspector() throws Exception {
        Fixture f = seedAuthorisedInspector();

        VisitCreateRequest req = new VisitCreateRequest();
        req.setInspectionNumber("INS-" + System.nanoTime());
        req.setInspectionTypeId(f.type().getInspectionTypeId());
        req.setEstablishmentId(f.estab().getEstablishmentId());
        req.setLicenseId(f.license().getLicenseId());
        req.setLicenseAvailable(true);
        req.setLicenseEntryMethod("LICENSE_NUMBER");
        req.setInspectionMode("ON_SITE");
        req.setLatitude(24.71);
        req.setLongitude(46.67);

        mockMvc.perform(post("/api/mobile/visits")
                        .param("inspectorId", f.inspector().getUserId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inspectionId").isNumber())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.currentStage").value("FIELD_INSPECTION"));
    }

    @Test
    void createVisit_returns400WhenInspectorNotAuthorisedForType() throws Exception {
        Fixture f = seedAuthorisedInspector();
        InspectionType otherType = testData.createInspectionType("FIRE", "Fire Safety", true);

        VisitCreateRequest req = new VisitCreateRequest();
        req.setInspectionNumber("INS-" + System.nanoTime());
        req.setInspectionTypeId(otherType.getInspectionTypeId());
        req.setEstablishmentId(f.estab().getEstablishmentId());
        req.setLicenseAvailable(false);

        mockMvc.perform(post("/api/mobile/visits")
                        .param("inspectorId", f.inspector().getUserId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "You are not authorised to conduct inspection type ID: " + otherType.getInspectionTypeId()));
    }

    // ─── POST /api/mobile/visits/facility ────────────────────────────────

    @Test
    void createFacility_succeedsWithValidIsicHierarchy() throws Exception {
        IsicActivity parent = testData.createIsicCategory("I-56", "Food & Beverage", "أنشطة");
        IsicActivity detail = testData.createIsicDetail("I-5610", "Restaurants", "مطاعم", parent);

        FacilityCreateRequest req = new FacilityCreateRequest();
        req.setFacilityName("Cafe Al-Nakheel");
        req.setLocation("King Fahd Road");
        req.setCity("Riyadh");
        req.setLatitude(24.71);
        req.setLongitude(46.67);
        req.setIsicId(parent.getIsicId());
        req.setIsicDetailId(detail.getIsicId());

        mockMvc.perform(post("/api/mobile/visits/facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilityName").value("Cafe Al-Nakheel"))
                .andExpect(jsonPath("$.licenseAvailable").value(false))
                .andExpect(jsonPath("$.detailActivityName").value("Restaurants"));
    }

    @Test
    void createFacility_returns400WhenDetailNotChildOfCategory() throws Exception {
        IsicActivity parentA = testData.createIsicCategory("I-56", "Food", "x");
        IsicActivity parentB = testData.createIsicCategory("G-47", "Retail", "y");
        IsicActivity detailB = testData.createIsicDetail("G-4711", "Stores", "z", parentB);

        FacilityCreateRequest req = new FacilityCreateRequest();
        req.setFacilityName("Mismatch");
        req.setLocation("Somewhere");
        req.setLatitude(24.71);
        req.setLongitude(46.67);
        req.setIsicId(parentA.getIsicId());        // food
        req.setIsicDetailId(detailB.getIsicId());  // child of retail

        mockMvc.perform(post("/api/mobile/visits/facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("is not a child of ISIC category")));
    }

    // ─── POST /api/mobile/visits/no-license ──────────────────────────────

    @Test
    void createNoLicenseVisit_createsFacilityAndVisitInOneShot() throws Exception {
        Fixture f = seedAuthorisedInspector();
        IsicActivity parent = testData.createIsicCategory("I-56", "Food", "x");
        IsicActivity detail = testData.createIsicDetail("I-5610", "Restaurants", "y", parent);

        FacilityCreateRequest facility = new FacilityCreateRequest();
        facility.setFacilityName("Pop-up Food Truck");
        facility.setLocation("Olaya");
        facility.setCity("Riyadh");
        facility.setLatitude(24.7);
        facility.setLongitude(46.6);
        facility.setIsicId(parent.getIsicId());
        facility.setIsicDetailId(detail.getIsicId());

        NoLicenseVisitRequest req = new NoLicenseVisitRequest();
        req.setInspectionNumber("INS-NL-" + System.nanoTime());
        req.setInspectionTypeId(f.type().getInspectionTypeId());
        req.setInspectionMode("ON_SITE");
        req.setFacility(facility);

        mockMvc.perform(post("/api/mobile/visits/no-license")
                        .param("inspectorId", f.inspector().getUserId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inspectionId").isNumber())
                .andExpect(jsonPath("$.establishmentName").value("Pop-up Food Truck"));
    }
}
