/// Violation returned from /api/mobile/visits/{id}/violations and /licenses/.../previous-violations
class ViolationDto {
  final int violationId;
  final int? clauseId;
  final String? clauseCode;
  final String? clauseTitle;
  final String? clauseTitleAr;
  final String? severity;
  final String? violationDescription;
  final String? correctiveAction;
  final String? status;        // NEEDS_DECISION, FIXED, etc.
  final DateTime? violationDate;

  const ViolationDto({
    required this.violationId,
    this.clauseId,
    this.clauseCode,
    this.clauseTitle,
    this.clauseTitleAr,
    this.severity,
    this.violationDescription,
    this.correctiveAction,
    this.status,
    this.violationDate,
  });

  factory ViolationDto.fromJson(Map<String, dynamic> j) => ViolationDto(
        violationId: (j['violationId'] ?? j['id'] ?? 0) as int,
        clauseId: j['clauseId'] as int?,
        clauseCode: (j['clauseCode'] ?? j['code']) as String?,
        clauseTitle: (j['clauseTitle'] ?? j['title']) as String?,
        clauseTitleAr: (j['clauseTitleAr'] ?? j['titleAr']) as String?,
        severity: j['severity'] as String?,
        violationDescription: (j['violationDescription'] ?? j['description']) as String?,
        correctiveAction: j['correctiveAction'] as String?,
        status: j['status'] as String?,
        violationDate: j['violationDate'] != null
            ? DateTime.tryParse(j['violationDate'].toString())
            : (j['createdAt'] != null ? DateTime.tryParse(j['createdAt'].toString()) : null),
      );
}

/// Body for POST /api/mobile/visits/{id}/violations.
/// Carries both the Sprint-1 fields (clauseId/severity/description/corrective)
/// and the Sprint-2 non-compliance bottom-sheet fields. Every non-compliance
/// field is optional so existing callers keep working unchanged.
class CreateViolationRequest {
  final int clauseId;
  final String severity;            // LOW / MEDIUM / HIGH
  final String violationDescription;
  final String? correctiveAction;

  // Non-compliance bottom-sheet fields (Sprint 2)
  final String? reasonCode;          // catalog id (e.g. "other", "no_license")
  final String? otherReason;         // populated when reasonCode == "other"
  final int? numberOfUnits;
  final String? offenderType;        // "contractor" | "license_owner"
  final String? penaltyCodes;        // CSV of penalty catalog ids
  final String? confiscatedProducts;
  final String? inspectorNotes;

  const CreateViolationRequest({
    required this.clauseId,
    required this.severity,
    required this.violationDescription,
    this.correctiveAction,
    this.reasonCode,
    this.otherReason,
    this.numberOfUnits,
    this.offenderType,
    this.penaltyCodes,
    this.confiscatedProducts,
    this.inspectorNotes,
  });

  Map<String, dynamic> toJson() => {
        'clauseId': clauseId,
        'severity': severity,
        'violationDescription': violationDescription,
        if (correctiveAction != null) 'correctiveAction': correctiveAction,
        if (reasonCode != null) 'reasonCode': reasonCode,
        if (otherReason != null && otherReason!.isNotEmpty)
          'otherReason': otherReason,
        if (numberOfUnits != null) 'numberOfUnits': numberOfUnits,
        if (offenderType != null) 'offenderType': offenderType,
        if (penaltyCodes != null && penaltyCodes!.isNotEmpty)
          'penaltyCodes': penaltyCodes,
        if (confiscatedProducts != null && confiscatedProducts!.isNotEmpty)
          'confiscatedProducts': confiscatedProducts,
        if (inspectorNotes != null && inspectorNotes!.isNotEmpty)
          'inspectorNotes': inspectorNotes,
      };
}
