package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.dto.IsicActivityResponse;
import com.aaseya.momthathel.service.IsicActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ISIC / Detail-Activity lookups for the no-license Facility-status screen (ISM-4).
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/mobile/isic-activities")
public class IsicActivityController {

    @Autowired
    private IsicActivityService isicService;

    @GetMapping
    public ResponseEntity<?> getTopLevel() {
        try {
            List<IsicActivityResponse> result = isicService.getTopLevelActivities();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/{parentId}/details")
    public ResponseEntity<?> getDetails(@PathVariable Long parentId) {
        try {
            List<IsicActivityResponse> result = isicService.getDetailActivities(parentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
