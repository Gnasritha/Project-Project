package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dto.ProcessStartResult;
import com.aaseya.momthathel.model.InspectionCase;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Starts and signals the {@code PerformInspection} BPMN process (Camunda 8.9).
 *
 * <p>The {@link CamundaClient} is injected with {@code required = false} so
 * the application still boots when Camunda is disabled
 * ({@code camunda.client.enabled=false}). With Camunda off, every method
 * here degrades gracefully — it returns {@code started=false} / logs a
 * warning — so the caller can decide how to surface it.</p>
 *
 * <p>Camunda 8 has no native "business key" field. In this app the
 * {@code inspectionNumber} (server-generated as
 * {@code INS-yyyy-{processInstanceKey}}) doubles as the human-facing
 * identifier and the Zeebe correlation key — it is pushed back into the
 * running instance as the {@code inspectionNumber} process variable once
 * the instance has been created.</p>
 */
@Service
public class InspectionProcessService {

    private static final Logger log = LoggerFactory.getLogger(InspectionProcessService.class);

    /** BPMN process id — must match {@code <bpmn:process id="...">} in PerformInspection.bpmn. */
    public static final String PERFORM_INSPECTION_PROCESS_ID = "PerformInspection";

    /** Message name a submitted case publishes; a catch event in the BPMN may subscribe to it. */
    public static final String CASE_SUBMITTED_MESSAGE = "CaseSubmitted";

    @Autowired(required = false)
    private CamundaClient camundaClient;

    /** True when a Camunda client bean is wired (camunda.client.enabled=true). */
    public boolean isCamundaAvailable() {
        return camundaClient != null;
    }

    /**
     * Starts a {@code PerformInspection} process instance with the given
     * initial variables. Never throws — failures are reported via the
     * returned {@link ProcessStartResult} (callers should check
     * {@link ProcessStartResult#isStarted()}).
     */
    public ProcessStartResult startPerformInspection(Map<String, Object> initialVariables) {
        ProcessStartResult result = new ProcessStartResult();

        if (camundaClient == null) {
            result.setStarted(false);
            result.setMessage("Camunda client disabled (camunda.client.enabled=false) — process not started.");
            log.warn("[BPMN] PerformInspection NOT started — Camunda client disabled.");
            return result;
        }

        try {
            Map<String, Object> vars = initialVariables != null ? initialVariables : defaultProcessVariables();

            ProcessInstanceEvent event = camundaClient.newCreateInstanceCommand()
                    .bpmnProcessId(PERFORM_INSPECTION_PROCESS_ID)
                    .latestVersion()
                    .variables(vars)
                    .send()
                    .join();

            result.setStarted(true);
            result.setProcessInstanceKey(event.getProcessInstanceKey());
            result.setProcessDefinitionKey(event.getProcessDefinitionKey());
            result.setVersion(event.getVersion());
            result.setMessage("PerformInspection process started");

            log.info("[BPMN] PerformInspection STARTED — processInstanceKey={}, processDefinitionKey={}, version={}",
                    event.getProcessInstanceKey(), event.getProcessDefinitionKey(), event.getVersion());
            return result;

        } catch (Exception e) {
            result.setStarted(false);
            result.setMessage("Failed to start PerformInspection: " + e.getMessage());
            log.error("[BPMN] PerformInspection FAILED to start: {}", e.getMessage(), e);
            return result;
        }
    }

    /**
     * Pushes the {@code inspectionNumber} into a running process instance
     * as a Zeebe variable so workers and Operate can correlate on it.
     * Mirrors the AIS / LMS post-start convention (the value can only be
     * computed after the process instance exists, because it embeds the
     * {@code processInstanceKey}).
     *
     * <p>Safe no-op when Camunda is unavailable; logs and swallows any
     * Zeebe-side failure so case persistence is unaffected.</p>
     */
    public void setInspectionNumberVariable(long processInstanceKey, String inspectionNumber) {
        if (camundaClient == null) {
            return;
        }
        try {
            Map<String, Object> vars = new HashMap<>();
            vars.put("inspectionNumber", inspectionNumber);
            camundaClient.newSetVariablesCommand(processInstanceKey)
                    .variables(vars)
                    .send()
                    .join();
            log.info("[BPMN] inspectionNumber variable set on process instance {} -> {}",
                    processInstanceKey, inspectionNumber);
        } catch (Exception e) {
            log.warn("[BPMN] Failed to set inspectionNumber on process {}: {}",
                    processInstanceKey, e.getMessage());
        }
    }

    /**
     * Best-effort notification that a case has been submitted. Publishes
     * the {@code CaseSubmitted} message correlated on the case
     * {@code inspectionNumber}, so a message catch event in the BPMN
     * (if present) can advance the flow.
     *
     * <p>Never throws — if Camunda is off, or no subscription exists yet,
     * the message is simply buffered/dropped by Zeebe and the API call
     * still succeeds.</p>
     */
    public void notifyCaseSubmitted(InspectionCase ic) {
        if (camundaClient == null) {
            log.warn("[BPMN] CaseSubmitted message NOT published for case {} — Camunda client disabled.",
                    ic.getInspectionId());
            return;
        }
        if (ic.getInspectionNumber() == null || ic.getInspectionNumber().isBlank()) {
            log.warn("[BPMN] CaseSubmitted message NOT published for case {} — no inspection number.",
                    ic.getInspectionId());
            return;
        }
        try {
            Map<String, Object> vars = new HashMap<>();
            vars.put("inspectionId", ic.getInspectionId());
            vars.put("inspectionNumber", ic.getInspectionNumber());
            vars.put("caseStatus", ic.getStatus());

            camundaClient.newPublishMessageCommand()
                    .messageName(CASE_SUBMITTED_MESSAGE)
                    .correlationKey(ic.getInspectionNumber())
                    .timeToLive(Duration.ofMinutes(30))
                    .variables(vars)
                    .send()
                    .join();

            log.info("[BPMN] CaseSubmitted message published — case={}, inspectionNumber={}",
                    ic.getInspectionId(), ic.getInspectionNumber());
        } catch (Exception e) {
            log.warn("[BPMN] CaseSubmitted message publish failed for case {} (inspectionNumber={}): {}",
                    ic.getInspectionId(), ic.getInspectionNumber(), e.getMessage());
        }
    }

    /**
     * Routing-flag defaults so no gateway in {@code PerformInspection}
     * dead-ends when the caller omits a flag.
     */
    public Map<String, Object> defaultProcessVariables() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("caseFromMobileApp", false);
        vars.put("isIGCommercialScenario", false);
        vars.put("isGHScheduled", false);
        vars.put("typeOfVisit", "ROUTINE");
        vars.put("isCampaignVisit", false);
        vars.put("isCRMVDIncident", false);
        vars.put("hasInspectors", true);
        vars.put("isRiskBasedDispatchNeeded", false);
        vars.put("hasFailedClauses", false);
        vars.put("hasPMClauses", false);
        vars.put("isQualityInspection", false);
        vars.put("isFieldSurvey", false);
        vars.put("isFromAdhocVD", false);
        vars.put("isCRMIncidentForCommercial", false);
        vars.put("isAdhocManualAssign", false);
        vars.put("isManualAssignAdhocCase", false);
        vars.put("isSPCVisit", false);
        vars.put("licenseExists", true);
        vars.put("complianceCertificateExists", true);
        vars.put("isCommercialWithLicense", true);
        vars.put("isHealthOrMarketInspection", false);
        vars.put("hasSupportingObserver", false);
        vars.put("observerHasAssociation", false);
        vars.put("hasSubclauses", false);
        vars.put("hasGeneralViolations", false);
        vars.put("isEstablishmentOpen", true);
        vars.put("isNewENFProcessFollowed", false);
        vars.put("caseExceededAfterSMS", false);
        vars.put("isLicenseCancelled", false);
        return vars;
    }
}
