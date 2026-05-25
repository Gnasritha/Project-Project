import 'clause_model.dart';

/// Clause from GET /api/mobile/clauses
class ClauseDto {
  final int id;
  final String code;
  final String title;
  final String? titleAr;
  final String? description;
  final String? descriptionAr;
  final String? severity;       // LOW / MEDIUM / HIGH
  final String? category;
  final String? categoryAr;

  const ClauseDto({
    required this.id,
    required this.code,
    required this.title,
    this.titleAr,
    this.description,
    this.descriptionAr,
    this.severity,
    this.category,
    this.categoryAr,
  });

  factory ClauseDto.fromJson(Map<String, dynamic> j) => ClauseDto(
        // Backend ClauseResponse sends clauseId / clauseCode / clauseName /
        // severityLevel — read those first, keep old keys as fallbacks.
        id: ((j['clauseId'] ?? j['id'] ?? 0) as num).toInt(),
        code: (j['clauseCode'] ?? j['code'] ?? '').toString(),
        title: (j['clauseName'] ?? j['title'] ?? j['name'] ?? j['titleEn'] ?? '').toString(),
        titleAr: (j['clauseNameAr'] ?? j['titleAr']) as String?,
        description: (j['description'] ?? j['descriptionEn']) as String?,
        descriptionAr: j['descriptionAr'] as String?,
        severity: (j['severityLevel'] ?? j['severity'] ?? j['risk']) as String?,
        category: j['category'] as String?,
        categoryAr: j['categoryAr'] as String?,
      );

  ClauseRisk get risk {
    switch ((severity ?? '').toUpperCase()) {
      case 'HIGH':
        return ClauseRisk.high;
      case 'MEDIUM':
      case 'MED':
        return ClauseRisk.medium;
      default:
        return ClauseRisk.low;
    }
  }
}
