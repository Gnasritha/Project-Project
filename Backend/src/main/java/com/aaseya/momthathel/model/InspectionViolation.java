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

    // ── Non-compliance bottom-sheet fields (V021) ───────────────────────────
    /** Catalog id of the reason for non-compliance (e.g. "other", "no_license"). */
    @Column(name = "reason_code", length = 50)
    private String reasonCode;

    /** Free-text reason, populated when {@code reasonCode == "other"}. */
    @Column(name = "other_reason", columnDefinition = "TEXT")
    private String otherReason;

    @Column(name = "number_of_units")
    private Integer numberOfUnits;

    /** Who the violation is attributed to — "contractor" / "license_owner". */
    @Column(name = "offender_type", length = 50)
    private String offenderType;

    /** CSV of selected penalty catalog ids (cancel_license, confiscate_goods, ...). */
    @Column(name = "penalty_codes", length = 500)
    private String penaltyCodes;

    @Column(name = "confiscated_products", length = 255)
    private String confiscatedProducts;

    /** Optional inspector's notes for reviewer / approver. */
    @Column(name = "inspector_notes", columnDefinition = "TEXT")
    private String inspectorNotes;

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
}
