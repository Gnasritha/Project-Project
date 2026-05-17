package com.aaseya.momthathel.dto;

public class ViolationResponse {

    private Long violationId;
    private Long inspectionId;
    private Long clauseId;
    private String clauseCode;
    private String clauseName;
    private String severity;
    private String violationDescription;
    private String correctiveAction;

    private boolean success;
    private String message;

    public Long getViolationId() { return violationId; }
    public void setViolationId(Long violationId) { this.violationId = violationId; }

    public Long getInspectionId() { return inspectionId; }
    public void setInspectionId(Long inspectionId) { this.inspectionId = inspectionId; }

    public Long getClauseId() { return clauseId; }
    public void setClauseId(Long clauseId) { this.clauseId = clauseId; }

    public String getClauseCode() { return clauseCode; }
    public void setClauseCode(String clauseCode) { this.clauseCode = clauseCode; }

    public String getClauseName() { return clauseName; }
    public void setClauseName(String clauseName) { this.clauseName = clauseName; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getViolationDescription() { return violationDescription; }
    public void setViolationDescription(String violationDescription) { this.violationDescription = violationDescription; }

    public String getCorrectiveAction() { return correctiveAction; }
    public void setCorrectiveAction(String correctiveAction) { this.correctiveAction = correctiveAction; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
