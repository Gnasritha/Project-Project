enum ControlType { collectiveHousing, markets, health }

class VisitModel {
  final String id;
  final String inspectionNumber;
  final ControlType type;
  final DateTime createdAt;
  final String? facilityNameAr;
  final String? facilityNameEn;

  const VisitModel({
    required this.id,
    required this.inspectionNumber,
    required this.type,
    required this.createdAt,
    this.facilityNameAr,
    this.facilityNameEn,
  });
}

class LicenseInfo {
  final String licenseNumber;
  final String facilityNameAr;
  final String facilityType;
  final String mobile;
  final String status;

  const LicenseInfo({
    required this.licenseNumber,
    required this.facilityNameAr,
    required this.facilityType,
    required this.mobile,
    required this.status,
  });
}
