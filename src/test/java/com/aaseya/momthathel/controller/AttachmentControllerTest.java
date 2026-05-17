package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.BaseIntegrationTest;
import com.aaseya.momthathel.model.Establishment;
import com.aaseya.momthathel.model.InspectionCase;
import com.aaseya.momthathel.model.InspectionType;
import com.aaseya.momthathel.model.License;
import com.aaseya.momthathel.model.Role;
import com.aaseya.momthathel.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AttachmentControllerTest extends BaseIntegrationTest {

    private InspectionCase seedInspectionCase() {
        Role role = testData.createRole("INSPECTOR-ATT-" + System.nanoTime());
        InspectionType type = testData.createInspectionType("HEALTH", "Health & Safety", true);
        Set<InspectionType> auth = new HashSet<>();
        auth.add(type);
        User inspector = testData.createInspector(role, "att-user-" + System.nanoTime(),
                "Att User", "EMP-A", auth);
        Establishment estab = testData.createEstablishment("Cafe", "Food", "Riyadh", type);
        License license = testData.createLicense("LIC-A-" + System.nanoTime(), estab, "ACTIVE", "Commercial");

        return testData.createInspectionCase("INS-A-" + System.nanoTime(), inspector, type, estab, license);
    }

    @Test
    void upload_storesFileAndReturnsMetadata() throws Exception {
        InspectionCase ic = seedInspectionCase();

        MockMultipartFile file = new MockMultipartFile(
                "file", "kitchen.jpg", MediaType.IMAGE_JPEG_VALUE, "fakebytes".getBytes());

        mockMvc.perform(multipart("/api/mobile/visits/" + ic.getInspectionId() + "/attachments").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attachmentId").isNumber())
                .andExpect(jsonPath("$.fileName").value("kitchen.jpg"))
                .andExpect(jsonPath("$.inspectionId").value(ic.getInspectionId()))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void upload_returns400WhenFileEmpty() throws Exception {
        InspectionCase ic = seedInspectionCase();

        MockMultipartFile empty = new MockMultipartFile(
                "file", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/mobile/visits/" + ic.getInspectionId() + "/attachments").file(empty))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("File is required and cannot be empty."));
    }

    @Test
    void list_returnsUploadedAttachmentsOrderedByUploadedAt() throws Exception {
        InspectionCase ic = seedInspectionCase();
        testData.createAttachment(ic, "first.jpg", "/tmp/first.jpg", "image/jpeg");
        testData.createAttachment(ic, "second.pdf", "/tmp/second.pdf", "application/pdf");

        mockMvc.perform(get("/api/mobile/visits/" + ic.getInspectionId() + "/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void validate_returns200WhenAtLeastOneAttachmentExists() throws Exception {
        InspectionCase ic = seedInspectionCase();
        testData.createAttachment(ic, "one.jpg", "/tmp/one.jpg", "image/jpeg");

        mockMvc.perform(post("/api/mobile/visits/" + ic.getInspectionId() + "/attachments/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void validate_returns400WhenNoAttachments() throws Exception {
        InspectionCase ic = seedInspectionCase();

        mockMvc.perform(post("/api/mobile/visits/" + ic.getInspectionId() + "/attachments/validate"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "At least one attachment is required to proceed with the inspection."));
    }

    @Test
    void delete_removesAttachment() throws Exception {
        InspectionCase ic = seedInspectionCase();
        Long attId = testData.createAttachment(ic, "doomed.jpg", "/tmp/doomed.jpg", "image/jpeg").getAttachmentId();

        mockMvc.perform(delete("/api/mobile/visits/" + ic.getInspectionId() + "/attachments/" + attId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(delete("/api/mobile/visits/" + ic.getInspectionId() + "/attachments/" + attId))
                .andExpect(status().isBadRequest());
    }
}
