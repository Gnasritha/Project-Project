package com.aaseya.momthathel.dto;

/**
 * Outcome of starting (or attempting to start) the {@code PerformInspection}
 * BPMN process instance.
 *
 * <p>Never represents a thrown exception — when Camunda is disabled or
 * unreachable, {@code started} is {@code false} and {@code message}
 * explains why, so the caller can decide how to surface it.</p>
 *
 * <p>The {@code processInstanceKey} is the only field needed to derive the
 * inspection number ({@code INS-yyyy-{processInstanceKey}}); other Zeebe
 * identifiers are exposed for logging.</p>
 */
public class ProcessStartResult {

    private boolean started;
    private Long processInstanceKey;
    private Long processDefinitionKey;
    private Integer version;
    private String message;

    public boolean isStarted() { return started; }
    public void setStarted(boolean started) { this.started = started; }

    public Long getProcessInstanceKey() { return processInstanceKey; }
    public void setProcessInstanceKey(Long processInstanceKey) { this.processInstanceKey = processInstanceKey; }

    public Long getProcessDefinitionKey() { return processDefinitionKey; }
    public void setProcessDefinitionKey(Long processDefinitionKey) { this.processDefinitionKey = processDefinitionKey; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
