package com.aaseya.momthathel.dto;

public class ClauseResponse {

    private Long clauseId;
    private String clauseCode;
    private String clauseName;
    private String severityLevel;

    public ClauseResponse() {}

    public ClauseResponse(Long clauseId, String clauseCode, String clauseName, String severityLevel) {
        this.clauseId = clauseId;
        this.clauseCode = clauseCode;
        this.clauseName = clauseName;
        this.severityLevel = severityLevel;
    }

    public Long getClauseId() { return clauseId; }
    public void setClauseId(Long clauseId) { this.clauseId = clauseId; }

    public String getClauseCode() { return clauseCode; }
    public void setClauseCode(String clauseCode) { this.clauseCode = clauseCode; }

    public String getClauseName() { return clauseName; }
    public void setClauseName(String clauseName) { this.clauseName = clauseName; }

    public String getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }
}
