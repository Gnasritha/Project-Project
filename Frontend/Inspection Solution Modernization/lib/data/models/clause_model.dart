enum ComplianceStatus { none, compliant, nonCompliant, notApplicable }

enum ClauseRisk { low, medium, high }

class ClauseModel {
  final String id;
  final String title;
  final String description;
  final ClauseRisk risk;
  ComplianceStatus status;
  String? reason;

  ClauseModel({
    required this.id,
    required this.title,
    required this.description,
    this.risk = ClauseRisk.low,
    this.status = ComplianceStatus.none,
    this.reason,
  });
}

class ClauseGroupModel {
  final String id;
  final String title;
  final String subtitle;
  final List<ClauseModel> clauses;

  const ClauseGroupModel({
    required this.id,
    required this.title,
    required this.subtitle,
    required this.clauses,
  });

  int get total => clauses.length;
}
