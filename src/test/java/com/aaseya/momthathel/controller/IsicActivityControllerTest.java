package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.BaseIntegrationTest;
import com.aaseya.momthathel.model.IsicActivity;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IsicActivityControllerTest extends BaseIntegrationTest {

    @Test
    void getTopLevel_returnsParentsOnlyAndSortedByNameEn() throws Exception {
        IsicActivity food = testData.createIsicCategory("I-56", "Food & Beverage", "x");
        IsicActivity retail = testData.createIsicCategory("G-47", "Retail Trade", "y");
        testData.createIsicDetail("I-5610", "Restaurants", "z", food); // should NOT appear

        mockMvc.perform(get("/api/mobile/isic-activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nameEn").value("Food & Beverage"))
                .andExpect(jsonPath("$[1].nameEn").value("Retail Trade"));
    }

    @Test
    void getDetails_returnsChildrenOfGivenCategory() throws Exception {
        IsicActivity food = testData.createIsicCategory("I-56", "Food", "x");
        testData.createIsicDetail("I-5610", "Restaurants", "y", food);
        testData.createIsicDetail("I-5630", "Beverage cafes", "z", food);

        IsicActivity retail = testData.createIsicCategory("G-47", "Retail", "x");
        testData.createIsicDetail("G-4711", "Stores", "y", retail);

        mockMvc.perform(get("/api/mobile/isic-activities/" + food.getIsicId() + "/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nameEn").value("Beverage cafes"))
                .andExpect(jsonPath("$[1].nameEn").value("Restaurants"));
    }

    @Test
    void getDetails_returnsEmptyListWhenNoChildren() throws Exception {
        IsicActivity solo = testData.createIsicCategory("X-99", "Lonely", "x");

        mockMvc.perform(get("/api/mobile/isic-activities/" + solo.getIsicId() + "/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
