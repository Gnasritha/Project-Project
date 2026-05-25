// Models backing the non-compliance / violation bottom-sheet chain
// (Sprint2 screenshots 1-5). Kept separate from the Sprint-1
// violation_model.dart so neither sprint's code is disturbed.

/// A bilingual id/label option used by dropdowns and checkbox lists.
///
/// Carries both an English and an Arabic label so the picked value follows
/// the app language — call [label] with the active locale's `isAr`.
class CatalogOption {
  final String id;
  final String labelEn;
  final String labelAr;

  const CatalogOption(this.id, this.labelEn, this.labelAr);

  /// Locale-aware label — pass `AppStrings.of(context).isAr`.
  String label(bool isAr) => isAr ? labelAr : labelEn;

  @override
  bool operator ==(Object other) =>
      other is CatalogOption && other.id == id;

  @override
  int get hashCode => id.hashCode;
}

/// Where the violator was during the inspection.
enum PresenceStatus { presentCooperative, presentUncooperative, absent }

/// A single specified violation with its penalties and consequence details.
class Violation {
  String code;
  int unitCount;
  CatalogOption? violator;
  List<String> penalties; // selected penalty ids
  bool hasConsequences;
  PresenceStatus? presenceStatus;
  CatalogOption? requiredAction;

  Violation({
    required this.code,
    this.unitCount = 1,
    this.violator,
    List<String>? penalties,
    this.hasConsequences = true,
    this.presenceStatus,
    this.requiredAction,
  }) : penalties = penalties ?? [];

  Map<String, dynamic> toJson() => {
        'code': code,
        'unitCount': unitCount,
        'violator': violator?.id,
        'penalties': penalties,
        'hasConsequences': hasConsequences,
        if (presenceStatus != null) 'presenceStatus': presenceStatus!.name,
        if (requiredAction != null) 'requiredAction': requiredAction!.id,
      };
}

/// One non-compliance record for a clause: reason + the violations raised.
class NonComplianceEntry {
  String clause;
  CatalogOption? reason;
  String otherReason;
  List<Violation> violations;
  String inspectorNotes;
  int attachmentCount;

  NonComplianceEntry({
    required this.clause,
    this.reason,
    this.otherReason = '',
    List<Violation>? violations,
    this.inspectorNotes = '',
    this.attachmentCount = 0,
  }) : violations = violations ?? [];

  Map<String, dynamic> toJson() => {
        'clause': clause,
        'reason': reason?.id,
        if (otherReason.isNotEmpty) 'otherReason': otherReason,
        'violations': violations.map((v) => v.toJson()).toList(),
        'inspectorNotes': inspectorNotes,
        'attachmentCount': attachmentCount,
      };
}

/// Static reference data for the violation sheets. Every option is bilingual
/// so it renders in the active app language. Swap for a real reference
/// endpoint when the API is ready — the screens depend only on this shape.
class MockViolationCatalog {
  MockViolationCatalog._();

  static const reasons = <CatalogOption>[
    CatalogOption('other',             'Other',                              'أخرى'),
    CatalogOption('no_license',        'No municipal license',               'عدم وجود ترخيص'),
    CatalogOption('expired_license',   'Expired license',                    'ترخيص منتهي'),
    CatalogOption('activity_mismatch', 'Activity not matching the license',  'نشاط غير مطابق للترخيص'),
  ];

  /// Violations offered on the "Specify violations" checkbox list.
  static const violationOptions = <CatalogOption>[
    CatalogOption('act_no_municipal', 'Practising an activity without a municipal license', 'ممارسة نشاط دون الحصول على ترخيص بلدي'),
    CatalogOption('act_no_license',   'Practising the activity without a license',          'مزاولة النشاط بدون الحصول على ترخيص'),
    CatalogOption('expired_permit',   'Practising the activity with an expired license',    'مزاولة النشاط برخصة منتهية'),
  ];

  static const violators = <CatalogOption>[
    CatalogOption('contractor',    'Contractor',    'المقاول'),
    CatalogOption('license_owner', 'License owner', 'مالك الترخيص'),
  ];

  static const subsequentPenalties = <CatalogOption>[
    CatalogOption('cancel_license',   'Cancel the license',                              'إلغاء الترخيص'),
    CatalogOption('confiscate_goods', 'Confiscate goods if food products have expired',  'مصادرة البضاعة في حال إنتهاء صلاحية المنتجات الغذائية'),
    CatalogOption('close_facility',   'Close the shop / facility',                       'إغلاق المحل / المنشأة'),
  ];

  /// Penalty ids that expose an "Add product" affordance on their row.
  static const penaltiesWithProduct = <String>{'confiscate_goods'};

  static const requiredActions = <CatalogOption>[
    CatalogOption('remove_or_restore', 'Remove the violation or restore the situation....', 'إزالة المخالفة أو إعادة الوضع....'),
    CatalogOption('warn',              'Issue a formal warning',                            'إصدار إنذار رسمي'),
    CatalogOption('escalate',          'Escalate to the supervising authority',             'التصعيد إلى الجهة المشرفة'),
  ];

  /// Resolves a locale-aware violation-code label for a selected option.
  static String codeForOption(String optionId, bool isAr) {
    final prefix = switch (optionId) {
      'act_no_municipal' => '1/1/1',
      'expired_permit' => '2/1/3',
      _ => '1/1/2',
    };
    final schedule = isAr
        ? 'جدول الجزاءات والمخالفات البلدية 1444 هـ'
        : 'Municipal Penalties & Violations Schedule 1444 AH';
    return '$prefix $schedule';
  }
}
