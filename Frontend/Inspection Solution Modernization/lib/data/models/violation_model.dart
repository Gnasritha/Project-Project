enum ViolationStatus { needsDecision, fixed }

class ViolationModel {
  final String id;
  final String titleAr;
  final String descriptionAr;
  final DateTime violationDate;
  final ViolationStatus status;

  const ViolationModel({
    required this.id,
    required this.titleAr,
    required this.descriptionAr,
    required this.violationDate,
    required this.status,
  });

  factory ViolationModel.fromJson(Map<String, dynamic> j) => ViolationModel(
        id: j['id'] as String,
        titleAr: j['titleAr'] as String,
        descriptionAr: j['descriptionAr'] as String,
        violationDate: DateTime.parse(j['violationDate'] as String),
        status: (j['status'] as String) == 'fixed'
            ? ViolationStatus.fixed
            : ViolationStatus.needsDecision,
      );
}
