package com.aaseya.momthathel.controller;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Starts BPMN process instances from REST.
 *
 * <p>Example payload — see README "Starting a case" section for full
 * variable list. At minimum: {@code inspectorId}, {@code caseFromMobileApp},
 * plus the routing booleans the gateways read.</p>
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/inspection")
public class InspectionProcessController {

    @Autowired(required = false)
    private CamundaClient camundaClient;

    @PostMapping("/start")
    public ResponseEntity<?> startInspectionProcess(@RequestBody Map<String, Object> variables) {

        Map<String, Object> response = new HashMap<>();

        if (camundaClient == null) {
            response.put("status", "error");
            response.put("message",
                    "Camunda client is disabled (camunda.client.enabled=false). Bring up Camunda 8.9 + Keycloak and re-enable in application.yml.");
            return ResponseEntity.status(503).body(response);
        }

        try {
            // Sensible defaults so a gateway doesn't dead-end if the caller omits a flag.
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

            // Caller overrides take precedence.
            if (variables != null) {
                vars.putAll(variables);
            }

            ProcessInstanceEvent event = camundaClient.newCreateInstanceCommand()
                    .bpmnProcessId("PerformInspection")
                    .latestVersion()
                    .variables(vars)
                    .send()
                    .join();

            response.put("status", "success");
            response.put("processInstanceKey", event.getProcessInstanceKey());
            response.put("processDefinitionKey", event.getProcessDefinitionKey());
            response.put("bpmnProcessId", event.getBpmnProcessId());
            response.put("version", event.getVersion());
            response.put("message", "PerformInspection process started");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
