import '../core/api/api_client.dart';

/// Result of a violator verification call. Always returned (success or
/// failure) — the [verified] flag tells the bottom sheet which banner to
/// render.
class ViolatorVerifyResult {
  final bool verified;
  final String? name;
  final String? idNumber;
  final String? nationalFacilityNumber;
  final String? mobileNumber;
  final String? message;

  const ViolatorVerifyResult({
    required this.verified,
    this.name,
    this.idNumber,
    this.nationalFacilityNumber,
    this.mobileNumber,
    this.message,
  });

  factory ViolatorVerifyResult.fromJson(Map<String, dynamic> j) =>
      ViolatorVerifyResult(
        verified: j['verified'] == true,
        name: j['name'] as String?,
        idNumber: j['idNumber'] as String?,
        nationalFacilityNumber: j['nationalFacilityNumber'] as String?,
        mobileNumber: j['mobileNumber'] as String?,
        message: j['message'] as String?,
      );
}

/// Backs the "Fill in violator data" bottom sheet — calls
/// `/api/mobile/violators/verify-individual` and `/verify-entity`.
class ViolatorService {
  ViolatorService._();
  static final instance = ViolatorService._();

  final _api = ApiClient.instance;

  /// Individual flow: 10-digit national ID + Gregorian birth date.
  Future<ViolatorVerifyResult> verifyIndividual({
    required String idNumber,
    required String birthDate,
  }) async {
    final body = await _api.post(
      '/api/mobile/violators/verify-individual',
      body: {'idNumber': idNumber, 'birthDate': birthDate},
    );
    return ViolatorVerifyResult.fromJson(_asMap(body));
  }

  /// Entity flow: national facility number.
  Future<ViolatorVerifyResult> verifyEntity({
    required String nationalFacilityNumber,
  }) async {
    final body = await _api.post(
      '/api/mobile/violators/verify-entity',
      body: {'nationalFacilityNumber': nationalFacilityNumber},
    );
    return ViolatorVerifyResult.fromJson(_asMap(body));
  }

  Map<String, dynamic> _asMap(dynamic body) =>
      body is Map<String, dynamic> ? body : <String, dynamic>{};
}
