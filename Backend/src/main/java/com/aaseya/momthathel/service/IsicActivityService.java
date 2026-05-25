package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dao.IsicActivityRepository;
import com.aaseya.momthathel.dto.IsicActivityResponse;
import com.aaseya.momthathel.model.IsicActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IsicActivityService {

    @Autowired
    private IsicActivityRepository isicActivityRepository;

    public List<IsicActivityResponse> getTopLevelActivities() {
        try {
            List<IsicActivity> rows = isicActivityRepository.findTopLevelActive();
            List<IsicActivityResponse> out = new ArrayList<>();
            for (IsicActivity ia : rows) {
                out.add(toResponse(ia));
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<IsicActivityResponse> getDetailActivities(Long parentId) {
        try {
            if (parentId == null) {
                throw new RuntimeException("parentId is required");
            }
            List<IsicActivity> rows = isicActivityRepository.findChildrenActive(parentId);
            List<IsicActivityResponse> out = new ArrayList<>();
            for (IsicActivity ia : rows) {
                out.add(toResponse(ia));
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private IsicActivityResponse toResponse(IsicActivity ia) {
        return new IsicActivityResponse(
                ia.getIsicId(),
                ia.getIsicCode(),
                ia.getNameEn(),
                ia.getNameAr(),
                ia.getParent() != null ? ia.getParent().getIsicId() : null,
                Boolean.TRUE.equals(ia.getActive()));
    }
}
