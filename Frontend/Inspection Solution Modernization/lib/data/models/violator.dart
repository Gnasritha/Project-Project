/// Whether a violator is a registered establishment or a natural person.
enum ViolatorCategory { entity, individual }

/// Result of the national-ID / facility-number verification call.
enum VerificationState { idle, verified, failed }

/// A violator attached to an inspection case. Backs the Violators Info screen
/// (Sprint2 screenshots 7-12). Mutable on purpose — the accordion card and the
/// data bottom sheet edit it in place.
class Violator {
  String name;
  ViolatorCategory category;

  /// null = the "Is the violator identified?" question hasn't been answered.
  bool? identified;

  int violationClauseCount;

  // Entity verification
  String? nationalFacilityNumber;

  // Individual verification
  String? idNumber;
  String? birthDate;

  VerificationState verification;

  /// true once the data bottom sheet has been completed for this violator.
  bool dataFilled;

  Violator({
    required this.name,
    this.category = ViolatorCategory.entity,
    this.identified,
    this.violationClauseCount = 0,
    this.nationalFacilityNumber,
    this.idNumber,
    this.birthDate,
    this.verification = VerificationState.idle,
    this.dataFilled = false,
  });

  Violator copy() => Violator(
        name: name,
        category: category,
        identified: identified,
        violationClauseCount: violationClauseCount,
        nationalFacilityNumber: nationalFacilityNumber,
        idNumber: idNumber,
        birthDate: birthDate,
        verification: verification,
        dataFilled: dataFilled,
      );

  /// API-ready serialization for the eventual visit-submit payload.
  Map<String, dynamic> toJson() => {
        'name': name,
        'category': category.name,
        'identified': identified,
        'violationClauseCount': violationClauseCount,
        if (nationalFacilityNumber != null)
          'nationalFacilityNumber': nationalFacilityNumber,
        if (idNumber != null) 'idNumber': idNumber,
        if (birthDate != null) 'birthDate': birthDate,
        'verified': verification == VerificationState.verified,
      };
}

/// Establishment owner shown on the Violators Info screen.
class OwnerInfo {
  final String name;
  final String nationalFacilityNumber;
  final String? mobileNumber;

  const OwnerInfo({
    required this.name,
    required this.nationalFacilityNumber,
    this.mobileNumber,
  });
}
