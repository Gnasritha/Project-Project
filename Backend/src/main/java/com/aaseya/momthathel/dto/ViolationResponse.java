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

    // ── Non-compliance bottom-sheet fields ──────────────────────────────────
    private String reasonCode;
    private String otherReason;
    private Integer numberOfUnits;
    private String offenderType;
    private String penaltyCodes;
    private String confiscatedProducts;
    private String inspectorNotes;

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

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getOtherReason() { return otherReason; }
    public void setOtherReason(String otherReason) { this.otherReason = otherReason; }

    public Integer getNumberOfUnits() { return numberOfUnits; }
    public void setNumberOfUnits(Integer numberOfUnits) { this.numberOfUnits = numberOfUnits; }

    public String getOffenderType() { return offenderType; }
    public void setOffenderType(String offenderType) { this.offenderType = offenderType; }

    public String getPenaltyCodes() { return penaltyCodes; }
    public void setPenaltyCodes(String penaltyCodes) { this.penaltyCodes = penaltyCodes; }

    public String getConfiscatedProducts() { return confiscatedProducts; }
    public void setConfiscatedProducts(String confiscatedProducts) { this.confiscatedProducts = confiscatedProducts; }

    public String getInspectorNotes() { return inspectorNotes; }
    public void setInspectorNotes(String inspectorNotes) { this.inspectorNotes = inspectorNotes; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
