package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.dto.AttachmentResponse;
import com.aaseya.momthathel.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inspection attachment endpoints (ISM-6).
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/mobile/visits/{inspectionId}/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> upload(@PathVariable Long inspectionId,
                                    @RequestParam("file") MultipartFile file) {
        try {
            AttachmentResponse result = attachmentService.upload(inspectionId, file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@PathVariable Long inspectionId) {
        try {
            List<AttachmentResponse> result = attachmentService.list(inspectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateMandatory(@PathVariable Long inspectionId) {
        try {
            attachmentService.assertHasAtLeastOneAttachment(inspectionId);
            Map<String, Object> ok = new HashMap<>();
            ok.put("status", "success");
            ok.put("message", "At least one attachment present");
            return ResponseEntity.ok(ok);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<?> delete(@PathVariable Long inspectionId,
                                    @PathVariable Long attachmentId) {
        try {
            attachmentService.delete(inspectionId, attachmentId);
            Map<String, Object> ok = new HashMap<>();
            ok.put("status", "success");
            ok.put("message", "Attachment deleted");
            return ResponseEntity.ok(ok);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
