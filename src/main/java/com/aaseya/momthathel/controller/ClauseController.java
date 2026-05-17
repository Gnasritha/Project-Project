package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.dto.ClauseResponse;
import com.aaseya.momthathel.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clauses catalogue for the violation-details screen (ISM-8).
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/mobile/clauses")
public class ClauseController {

    @Autowired
    private ViolationService violationService;

    @GetMapping
    public ResponseEntity<?> listClauses() {
        try {
            List<ClauseResponse> result = violationService.listClauses();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
