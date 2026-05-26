package com.aaseya.momthathel.dto;

/**
 * Body for {@code POST /api/mobile/visits/{inspectionId}/violations}.
 *
 * <p>Mirrors the fields collected in the "Non-compliance reasons" bottom
 * sheet (Sprint-2 screenshots 1-5): the clause-level reason / inspector
 * notes plus per-violation unit count, offender, penalties and
 * confiscated products. All non-compliance fields are optional so the
 * Sprint-1 callers (which only sent clauseId / severity / description /
 * correctiveAction) keep working unchanged.</p>
 */
public class ViolationCreateRequest {

    private Long clauseId;
    private String severity;
    private String violationDescription;
    private String correctiveAction;

    // ── Non-compliance bottom-sheet fields ──────────────────────────────────
    /** Catalog id of the non-compliance reason (e.g. "other", "no_license"). */
    private String reasonCode;
    /** Free-text reason when {@code reasonCode == "other"}. */
    private String otherReason;
    private Integer numberOfUnits;
    /** "contractor" | "license_owner". */
    private String offenderType;
    /** CSV of penalty catalog ids (e.g. "cancel_license,confiscate_goods"). */
    private String penaltyCodes;
    private String confiscatedProducts;
    /** Optional inspector's notes for reviewer / approver. */
    private String inspectorNotes;

    public Long getClauseId() { return clauseId; }
    public void setClauseId(Long clauseId) { this.clauseId = clauseId; }

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
}
