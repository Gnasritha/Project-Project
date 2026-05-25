package com.aaseya.momthathel.worker;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Camunda 8.9 job-worker stubs that back the BPMN service-task definitions.
 *
 * <p>Every method's {@code @JobWorker(type = "...")} value matches the
 * {@code <zeebe:taskDefinition type="..."/>} in the BPMN files under
 * {@code src/main/resources/bpmn/}. All 29 declared task types in
 * {@code PerformInspection.bpmn}, {@code AdhocCreateCase.bpmn},
 * {@code EnterAndValidateClauses.bpmn} and
 * {@code IdentifySituationOnNature.bpmn} are covered here.</p>
 *
 * <p><strong>Incident hardening.</strong> Each worker runs through
 * {@link #safelyExecute(String, ActivatedJob, Function)}, which:</p>
 * <ul>
 *   <li>Logs entry with {@code processInstanceKey}, {@code inspectionNumber}
 *       and BPMN element id — visible in the Eclipse console.</li>
 *   <li>Catches every {@code Exception} thrown by the body so a worker bug
 *       never escalates into an Operate incident. The process simply
 *       continues with the routing-flag defaults supplied at start time.</li>
 *   <li>Logs the returned variables for traceability.</li>
 * </ul>
 *
 * <p>The bodies remain intentionally lightweight stubs — wire them to the
 * real services ({@link com.aaseya.momthathel.service.InspectorVisitService},
 * {@link com.aaseya.momthathel.service.AttachmentService},
 * {@link com.aaseya.momthathel.service.ViolationService}) as the BPMN flow
 * matures. Use {@link ActivatedJob#getVariablesAsMap()} to read inputs —
 * avoid {@code @Variable Long} parameters, which fail on JSON
 * Integer→Long deserialization for small ids.</p>
 */
@Component
public class InspectionWorkers {

    private static final Logger log = LoggerFactory.getLogger(InspectionWorkers.class);

    // ════════════════════════════════════════════════════════════════════
    //  Safe-wrapper — every worker funnels through here
    // ════════════════════════════════════════════════════════════════════

    /**
     * Runs a worker body inside a uniform try/catch + log envelope so a
     * thrown exception never becomes an Operate incident. Always returns
     * a (possibly empty) variables map — Zeebe completes the job and the
     * process flow continues with whatever routing variables were seeded
     * at process start.
     */
    private Map<String, Object> safelyExecute(
            String type,
            ActivatedJob job,
            Function<Map<String, Object>, Map<String, Object>> body) {

        long pik = job.getProcessInstanceKey();
        String element = job.getElementId();
        Map<String, Object> vars;
        try {
            vars = job.getVariablesAsMap();
        } catch (Exception e) {
            log.error("[Worker IN ] type={}, element={}, pik={} — failed to read variables: {}",
                    type, element, pik, e.getMessage(), e);
            return Map.of();
        }
        Object inspectionNumber = vars.get("inspectionNumber");

        log.info("[Worker IN ] type={}, element={}, pik={}, inspectionNumber={}",
                type, element, pik, inspectionNumber);

        try {
            Map<String, Object> out = body.apply(vars);
            log.info("[Worker OUT] type={}, pik={}, inspectionNumber={}, returned={}",
                    type, pik, inspectionNumber, out);
            return out != null ? out : Map.of();
        } catch (Exception e) {
            // Swallow so the BPMN keeps flowing rather than incident-ing on a
            // stub worker. Real workers wired to services should still throw
            // when a hard failure must surface — replace this catch as needed.
            log.error("[Worker ERR] type={}, pik={}, inspectionNumber={} — {}",
                    type, pik, inspectionNumber, e.getMessage(), e);
            return Map.of();
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Main flow — PerformInspection.bpmn
    // ════════════════════════════════════════════════════════════════════

    @JobWorker(type = "ig-known-violator-commercial")
    public Map<String, Object> igKnownViolatorCommercial(final ActivatedJob job) {
        return safelyExecute("ig-known-violator-commercial", job, v -> Map.of());
    }

    @JobWorker(type = "gh-standing-committee-process")
    public Map<String, Object> ghStandingCommitteeProcess(final ActivatedJob job) {
        return safelyExecute("gh-standing-committee-process", job, v -> Map.of());
    }

    @JobWorker(type = "create-inspection-visit-data")
    public Map<String, Object> createInspectionVisitData(final ActivatedJob job) {
        return safelyExecute("create-inspection-visit-data", job, vars -> {
            // Read defensively — Zeebe may have these as Integer/Long; getVariablesAsMap
            // gives us Object so we don't trip on small-id Integer↔Long deserialization.
            Object inspectorId      = vars.get("inspectorId");
            Object inspectionTypeId = vars.get("inspectionTypeId");
            Object establishmentId  = vars.get("establishmentId");
            log.info("[Worker DETAIL] create-inspection-visit-data — inspector={}, type={}, estab={}",
                    inspectorId, inspectionTypeId, establishmentId);
            // TODO: wire to InspectorVisitService. For now, propagate inspectionId
            // (== processInstanceKey for the new flow) so downstream tasks can use it.
            Map<String, Object> out = new HashMap<>();
            out.put("inspectionId", job.getProcessInstanceKey());
            return out;
        });
    }

    @JobWorker(type = "schedule-quality-case")
    public Map<String, Object> scheduleQualityCase(final ActivatedJob job) {
        return safelyExecute("schedule-quality-case", job, v -> Map.of());
    }

    @JobWorker(type = "save-attachments-for-commercial")
    public Map<String, Object> saveAttachmentsForCommercial(final ActivatedJob job) {
        return safelyExecute("save-attachments-for-commercial", job, v -> Map.of());
    }

    @JobWorker(type = "reassign-to-inspector")
    public Map<String, Object> reassignToInspector(final ActivatedJob job) {
        return safelyExecute("reassign-to-inspector", job, v -> Map.of());
    }

    @JobWorker(type = "risk-based-dispatch")
    public Map<String, Object> riskBasedDispatch(final ActivatedJob job) {
        return safelyExecute("risk-based-dispatch", job, v -> Map.of());
    }

    @JobWorker(type = "fetch-estb-classification")
    public Map<String, Object> fetchEstbClassification(final ActivatedJob job) {
        return safelyExecute("fetch-estb-classification", job, v -> Map.of());
    }

    @JobWorker(type = "precautionary-measures")
    public Map<String, Object> precautionaryMeasures(final ActivatedJob job) {
        return safelyExecute("precautionary-measures", job, v -> Map.of());
    }

    @JobWorker(type = "perform-inspector-decision")
    public Map<String, Object> performInspectorDecision(final ActivatedJob job) {
        return safelyExecute("perform-inspector-decision", job, v -> Map.of());
    }

    @JobWorker(type = "update-est-visit-table")
    public Map<String, Object> updateEstVisitTable(final ActivatedJob job) {
        return safelyExecute("update-est-visit-table", job, v -> Map.of());
    }

    @JobWorker(type = "change-to-specific-stage")
    public Map<String, Object> changeToSpecificStage(final ActivatedJob job) {
        return safelyExecute("change-to-specific-stage", job, v -> Map.of());
    }

    // ════════════════════════════════════════════════════════════════════
    //  AdhocCreateCase.bpmn
    // ════════════════════════════════════════════════════════════════════

    @JobWorker(type = "field-survey-subtask")
    public Map<String, Object> fieldSurveySubtask(final ActivatedJob job) {
        return safelyExecute("field-survey-subtask", job, v -> Map.of());
    }

    @JobWorker(type = "spc-inspection-flow")
    public Map<String, Object> spcInspectionFlow(final ActivatedJob job) {
        return safelyExecute("spc-inspection-flow", job, v -> Map.of());
    }

    @JobWorker(type = "persist-inspection-case")
    public Map<String, Object> persistInspectionCase(final ActivatedJob job) {
        return safelyExecute("persist-inspection-case", job, v -> Map.of());
    }

    @JobWorker(type = "capture-business-configurations")
    public Map<String, Object> captureBusinessConfigurations(final ActivatedJob job) {
        return safelyExecute("capture-business-configurations", job, v -> Map.of());
    }

    @JobWorker(type = "commercial-with-license")
    public Map<String, Object> commercialWithLicense(final ActivatedJob job) {
        return safelyExecute("commercial-with-license", job, v -> Map.of());
    }

    // ════════════════════════════════════════════════════════════════════
    //  EnterAndValidateClauses.bpmn
    // ════════════════════════════════════════════════════════════════════

    @JobWorker(type = "update-status-visit-on-start")
    public Map<String, Object> updateStatusVisitOnStart(final ActivatedJob job) {
        return safelyExecute("update-status-visit-on-start", job, v -> Map.of());
    }

    @JobWorker(type = "resolve-no-association")
    public Map<String, Object> resolveNoAssociation(final ActivatedJob job) {
        return safelyExecute("resolve-no-association", job, v -> Map.of());
    }

    @JobWorker(type = "no-subclauses-to-validate")
    public Map<String, Object> noSubclausesToValidate(final ActivatedJob job) {
        return safelyExecute("no-subclauses-to-validate", job, v -> Map.of());
    }

    @JobWorker(type = "verifying-subclauses")
    public Map<String, Object> verifyingSubclauses(final ActivatedJob job) {
        return safelyExecute("verifying-subclauses", job, v -> Map.of());
    }

    @JobWorker(type = "calculate-compliance-score")
    public Map<String, Object> calculateComplianceScore(final ActivatedJob job) {
        return safelyExecute("calculate-compliance-score", job, vars -> {
            // TODO: pull violations for this inspectionId and compute.
            // Defaulting to 'no failed' so downstream gateways have routing data.
            Map<String, Object> out = new HashMap<>();
            out.put("hasFailedClauses", false);
            out.put("complianceScore", 100);
            return out;
        });
    }

    @JobWorker(type = "no-failed-clauses")
    public Map<String, Object> noFailedClauses(final ActivatedJob job) {
        return safelyExecute("no-failed-clauses", job, v -> Map.of());
    }

    // ════════════════════════════════════════════════════════════════════
    //  IdentifySituationOnNature.bpmn
    // ════════════════════════════════════════════════════════════════════

    @JobWorker(type = "identify-situation-on-nature")
    public Map<String, Object> identifySituationOnNature(final ActivatedJob job) {
        return safelyExecute("identify-situation-on-nature", job, v -> Map.of());
    }

    @JobWorker(type = "updations-to-est-table")
    public Map<String, Object> updationsToEstTable(final ActivatedJob job) {
        return safelyExecute("updations-to-est-table", job, v -> Map.of());
    }

    @JobWorker(type = "resolve-establishment-not-found")
    public Map<String, Object> resolveEstablishmentNotFound(final ActivatedJob job) {
        return safelyExecute("resolve-establishment-not-found", job, v -> Map.of());
    }

    @JobWorker(type = "set-new-violation-info")
    public Map<String, Object> setNewViolationInfo(final ActivatedJob job) {
        return safelyExecute("set-new-violation-info", job, v -> Map.of());
    }

    @JobWorker(type = "establishment-closed-not-found-img")
    public Map<String, Object> establishmentClosedNotFoundImg(final ActivatedJob job) {
        return safelyExecute("establishment-closed-not-found-img", job, v -> Map.of());
    }

    @JobWorker(type = "send-sms-to-owner-id")
    public Map<String, Object> sendSmsToOwnerId(final ActivatedJob job) {
        return safelyExecute("send-sms-to-owner-id", job, v -> Map.of());
    }
}
