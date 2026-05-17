package com.aaseya.momthathel.worker;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Camunda 8.9 job-worker stubs that back the BPMN service-task definitions.
 *
 * <p>Each method's {@code @JobWorker(type = "...")} value matches the
 * {@code <zeebe:taskDefinition type="..."/>} in the BPMN files under
 * {@code src/main/resources/bpmn/}.</p>
 *
 * <p>Workers are intentionally lightweight — they log, set decision-routing
 * variables back into the process scope, and complete. Replace the bodies
 * with calls to {@link com.aaseya.momthathel.service.InspectorVisitService},
 * {@link com.aaseya.momthathel.service.AttachmentService}, and
 * {@link com.aaseya.momthathel.service.ViolationService} as the BPMN flow
 * matures.</p>
 */
@Component
public class InspectionWorkers {

    private static final Logger log = LoggerFactory.getLogger(InspectionWorkers.class);

    // ════════════════════════════════════════════════════════════════════
    //  Main flow — PerformInspection.bpmn
    // ════════════════════════════════════════════════════════════════════

    @JobWorker(type = "ig-known-violator-commercial")
    public Map<String, Object> igKnownViolatorCommercial(final ActivatedJob job) {
        log.info("[Worker] ig-known-violator-commercial — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "gh-standing-committee-process")
    public Map<String, Object> ghStandingCommitteeProcess(final ActivatedJob job) {
        log.info("[Worker] gh-standing-committee-process — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "create-inspection-visit-data")
    public Map<String, Object> createInspectionVisitData(final ActivatedJob job,
                                                         @Variable(name = "inspectorId") Long inspectorId,
                                                         @Variable(name = "inspectionTypeId") Long inspectionTypeId,
                                                         @Variable(name = "establishmentId") Long establishmentId) {
        log.info("[Worker] create-inspection-visit-data — inspector={}, type={}, estab={}",
                inspectorId, inspectionTypeId, establishmentId);

        // TODO: wire to InspectorVisitService.createVisit(...)
        Map<String, Object> out = new HashMap<>();
        out.put("inspectionId", job.getProcessInstanceKey()); // placeholder
        return out;
    }

    @JobWorker(type = "schedule-quality-case")
    public Map<String, Object> scheduleQualityCase(final ActivatedJob job) {
        log.info("[Worker] schedule-quality-case — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "save-attachments-for-commercial")
    public Map<String, Object> saveAttachmentsForCommercial(final ActivatedJob job) {
        log.info("[Worker] save-attachments-for-commercial — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "reassign-to-inspector")
    public Map<String, Object> reassignToInspector(final ActivatedJob job) {
        log.info("[Worker] reassign-to-inspector — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "risk-based-dispatch")
    public Map<String, Object> riskBasedDispatch(final ActivatedJob job) {
        log.info("[Worker] risk-based-dispatch — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "fetch-estb-classification")
    public Map<String, Object> fetchEstbClassification(final ActivatedJob job) {
        log.info("[Worker] fetch-estb-classification — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "precautionary-measures")
    public Map<String, Object> precautionaryMeasures(final ActivatedJob job) {
        log.info("[Worker] precautionary-measures — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "perform-inspector-decision")
    public Map<String, Object> performInspectorDecision(final ActivatedJob job) {
        log.info("[Worker] perform-inspector-decision — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "update-est-visit-table")
    public Map<String, Object> updateEstVisitTable(final ActivatedJob job) {
        log.info("[Worker] update-est-visit-table — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "change-to-specific-stage")
    public Map<String, Object> changeToSpecificStage(final ActivatedJob job) {
        log.info("[Worker] change-to-specific-stage — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    // ════════════════════════════════════════════════════════════════════
    //  AdhocCreateCase.bpmn
    // ════════════════════════════════════════════════════════════════════

    @JobWorker(type = "field-survey-subtask")
    public Map<String, Object> fieldSurveySubtask(final ActivatedJob job) {
        log.info("[Worker] field-survey-subtask — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "spc-inspection-flow")
    public Map<String, Object> spcInspectionFlow(final ActivatedJob job) {
        log.info("[Worker] spc-inspection-flow — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "persist-inspection-case")
    public Map<String, Object> persistInspectionCase(final ActivatedJob job) {
        log.info("[Worker] persist-inspection-case — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "capture-business-configurations")
    public Map<String, Object> captureBusinessConfigurations(final ActivatedJob job) {
        log.info("[Worker] capture-business-configurations — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "commercial-with-license")
    public Map<String, Object> commercialWithLicense(final ActivatedJob job) {
        log.info("[Worker] commercial-with-license — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    // ════════════════════════════════════════════════════════════════════
    //  EnterAndValidateClauses.bpmn
    // ════════════════════════════════════════════════════════════════════

    @JobWorker(type = "update-status-visit-on-start")
    public Map<String, Object> updateStatusVisitOnStart(final ActivatedJob job) {
        log.info("[Worker] update-status-visit-on-start — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "resolve-no-association")
    public Map<String, Object> resolveNoAssociation(final ActivatedJob job) {
        log.info("[Worker] resolve-no-association — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "no-subclauses-to-validate")
    public Map<String, Object> noSubclausesToValidate(final ActivatedJob job) {
        log.info("[Worker] no-subclauses-to-validate — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "verifying-subclauses")
    public Map<String, Object> verifyingSubclauses(final ActivatedJob job) {
        log.info("[Worker] verifying-subclauses — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "calculate-compliance-score")
    public Map<String, Object> calculateComplianceScore(final ActivatedJob job) {
        log.info("[Worker] calculate-compliance-score — case={}", job.getProcessInstanceKey());
        // TODO: pull violations for this inspectionId and compute. Defaulting to 'no failed' for now.
        Map<String, Object> out = new HashMap<>();
        out.put("hasFailedClauses", false);
        out.put("complianceScore", 100);
        return out;
    }

    @JobWorker(type = "no-failed-clauses")
    public Map<String, Object> noFailedClauses(final ActivatedJob job) {
        log.info("[Worker] no-failed-clauses — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    // ════════════════════════════════════════════════════════════════════
    //  IdentifySituationOnNature.bpmn
    // ════════════════════════════════════════════════════════════════════

    @JobWorker(type = "identify-situation-on-nature")
    public Map<String, Object> identifySituationOnNature(final ActivatedJob job) {
        log.info("[Worker] identify-situation-on-nature — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "updations-to-est-table")
    public Map<String, Object> updationsToEstTable(final ActivatedJob job) {
        log.info("[Worker] updations-to-est-table — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "resolve-establishment-not-found")
    public Map<String, Object> resolveEstablishmentNotFound(final ActivatedJob job) {
        log.info("[Worker] resolve-establishment-not-found — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "set-new-violation-info")
    public Map<String, Object> setNewViolationInfo(final ActivatedJob job) {
        log.info("[Worker] set-new-violation-info — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "establishment-closed-not-found-img")
    public Map<String, Object> establishmentClosedNotFoundImg(final ActivatedJob job) {
        log.info("[Worker] establishment-closed-not-found-img — case={}", job.getProcessInstanceKey());
        return Map.of();
    }

    @JobWorker(type = "send-sms-to-owner-id")
    public Map<String, Object> sendSmsToOwnerId(final ActivatedJob job) {
        log.info("[Worker] send-sms-to-owner-id — case={}", job.getProcessInstanceKey());
        return Map.of();
    }
}
