package com.aaseya.momthathel.dto;

import java.time.LocalDateTime;

public class PreviousViolationDto {

    private Long violationId;
    private Long inspectionId;
    private String inspectionNumber;
    private LocalDateTime inspectionDate;
    private Long clauseId;
    private String clauseCode;
    private String clauseName;
    private String severity;
    private String violationDescription;
    private String correctiveAction;

    public PreviousViolationDto() {}

    public PreviousViolationDto(Long violationId, Long inspectionId, String inspectionNumber,
                                LocalDateTime inspectionDate, Long clauseId, String clauseCode,
                                String clauseName, String severity, String violationDescription,
                                String correctiveAction) {
        this.violationId = violationId;
        this.inspectionId = inspectionId;
        this.inspectionNumber = inspectionNumber;
        this.inspectionDate = inspectionDate;
        this.clauseId = clauseId;
        this.clauseCode = clauseCode;
        this.clauseName = clauseName;
        this.severity = severity;
        this.violationDescription = violationDescription;
        this.correctiveAction = correctiveAction;
    }

    public Long getViolationId() { return violationId; }
    public void setViolationId(Long violationId) { this.violationId = violationId; }

    public Long getInspectionId() { return inspectionId; }
    public void setInspectionId(Long inspectionId) { this.inspectionId = inspectionId; }

    public String getInspectionNumber() { return inspectionNumber; }
    public void setInspectionNumber(String inspectionNumber) { this.inspectionNumber = inspectionNumber; }

    public LocalDateTime getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDateTime inspectionDate) { this.inspectionDate = inspectionDate; }

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
}
