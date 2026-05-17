package com.aaseya.momthathel.controller;

import com.aaseya.momthathel.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClauseControllerTest extends BaseIntegrationTest {

    @Test
    void listClauses_returnsAllClausesSortedByCode() throws Exception {
        testData.createClause("C-2.01", "Fire exits blocked", "HIGH");
        testData.createClause("C-1.04", "Refrigeration inadequate", "HIGH");
        testData.createClause("C-3.07", "Pest control records missing", "MEDIUM");

        mockMvc.perform(get("/api/mobile/clauses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].clauseCode").value("C-1.04"))
                .andExpect(jsonPath("$[1].clauseCode").value("C-2.01"))
                .andExpect(jsonPath("$[2].clauseCode").value("C-3.07"));
    }

    @Test
    void listClauses_returnsEmptyListWhenNoClauses() throws Exception {
        mockMvc.perform(get("/api/mobile/clauses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
