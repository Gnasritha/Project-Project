/// Top-level ISIC category from GET /api/mobile/isic-activities
class IsicActivity {
  final int id;
  final String name;
  final String? nameAr;
  final String? code;

  const IsicActivity({required this.id, required this.name, this.nameAr, this.code});

  factory IsicActivity.fromJson(Map<String, dynamic> j) => IsicActivity(
        // Backend IsicActivityResponse sends isicId / isicCode / nameEn.
        id: ((j['isicId'] ?? j['id'] ?? 0) as num).toInt(),
        name: (j['nameEn'] ?? j['name'] ?? '').toString(),
        nameAr: j['nameAr'] as String?,
        code: (j['isicCode'] ?? j['code']) as String?,
      );
}

/// Detail activity from GET /api/mobile/isic-activities/{id}/details
class IsicDetailActivity {
  final int id;
  final String name;
  final String? nameAr;
  final int? parentId;

  const IsicDetailActivity({
    required this.id,
    required this.name,
    this.nameAr,
    this.parentId,
  });

  factory IsicDetailActivity.fromJson(Map<String, dynamic> j) => IsicDetailActivity(
        // Backend IsicActivityResponse sends isicId / nameEn / parentId.
        id: ((j['isicId'] ?? j['id'] ?? 0) as num).toInt(),
        name: (j['nameEn'] ?? j['name'] ?? '').toString(),
        nameAr: j['nameAr'] as String?,
        parentId: (j['parentId'] as num?)?.toInt(),
      );
}
