package com.aaseya.momthathel.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps to the {@code CLAUSE} table.
 */
@Entity
@Table(name = "clause")
public class Clause {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clause_seq")
    @SequenceGenerator(name = "clause_seq", sequenceName = "clause_clause_id_seq", allocationSize = 1)
    @Column(name = "clause_id")
    private Long clauseId;

    @Column(name = "clause_code", nullable = false, unique = true, length = 50)
    private String clauseCode;

    @Column(name = "clause_name", nullable = false, length = 300)
    private String clauseName;

    @Column(name = "severity_level", length = 50)
    private String severityLevel;

    @OneToMany(mappedBy = "clause", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InspectionViolation> violations = new ArrayList<>();

    public Clause() {}

    public Long getClauseId() { return clauseId; }
    public void setClauseId(Long clauseId) { this.clauseId = clauseId; }

    public String getClauseCode() { return clauseCode; }
    public void setClauseCode(String clauseCode) { this.clauseCode = clauseCode; }

    public String getClauseName() { return clauseName; }
    public void setClauseName(String clauseName) { this.clauseName = clauseName; }

    public String getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }

    public List<InspectionViolation> getViolations() { return violations; }
    public void setViolations(List<InspectionViolation> violations) { this.violations = violations; }
}
