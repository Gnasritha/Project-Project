package com.aaseya.momthathel.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Generates the unique <strong>business key</strong> carried by an inspection
 * case both in the local database ({@code inspection_case.business_key}) and
 * as the Camunda process variable {@code businessKey} on the
 * {@code PerformInspection} instance.
 *
 * <p>Camunda 8 / Zeebe has no native "business key" field (unlike Camunda 7),
 * so the convention is to pass it as a process variable. This key is what an
 * operator searches for in Operate, and what job workers use to correlate a
 * running process instance back to its {@code inspection_case} row.</p>
 *
 * <p>Format: {@code INS-yyyyMMdd-NNNNN} — e.g. {@code INS-20260521-00042}.
 * The numeric suffix is the database {@code inspection_id}, so the key is
 * unique by construction and reads straight back to the owning row.</p>
 */
@Component
public class ProcessBusinessKeyGenerator {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Builds the business key for a persisted inspection case.
     *
     * @param inspectionId the database primary key of the saved case
     * @return a unique, human-readable business key
     */
    public String generate(Long inspectionId) {
        if (inspectionId == null) {
            throw new IllegalArgumentException("inspectionId is required to generate a business key");
        }
        return String.format("INS-%s-%05d", LocalDate.now().format(DAY), inspectionId);
    }
}
