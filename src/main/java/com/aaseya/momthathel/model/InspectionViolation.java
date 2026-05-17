package com.aaseya.momthathel.model;

import jakarta.persistence.*;

/**
 * Maps to the {@code INSPECTION_VIOLATION} table.
 */
@Entity
@Table(name = "inspection_violation")
public class InspectionViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inspection_violation_seq")
    @SequenceGenerator(name = "inspection_violation_seq", sequenceName = "inspection_violation_violation_id_seq", allocationSize = 1)
    @Column(name = "violation_id")
    private Long violationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_id", nullable = false, foreignKey = @ForeignKey(name = "fk_violation_inspection"))
    private InspectionCase inspectionCase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clause_id", nullable = false, foreignKey = @ForeignKey(name = "fk_violation_clause"))
    private Clause clause;

    @Column(name = "violation_description", columnDefinition = "TEXT")
    private String violationDescription;

    @Column(name = "severity", length = 50)
    private String severity;

    @Column(name = "corrective_action", columnDefinition = "TEXT")
    private String correctiveAction;

    public InspectionViolation() {}

    public Long getViolationId() { return violationId; }
    public void setViolationId(Long violationId) { this.violationId = violationId; }

    public InspectionCase getInspectionCase() { return inspectionCase; }
    public void setInspectionCase(InspectionCase inspectionCase) { this.inspectionCase = inspectionCase; }

    public Clause getClause() { return clause; }
    public void setClause(Clause clause) { this.clause = clause; }

    public String getViolationDescription() { return violationDescription; }
    public void setViolationDescription(String violationDescription) { this.violationDescription = violationDescription; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getCorrectiveAction() { return correctiveAction; }
    public void setCorrectiveAction(String correctiveAction) { this.correctiveAction = correctiveAction; }
}
