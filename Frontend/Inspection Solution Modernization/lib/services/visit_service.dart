import '../core/api/api_client.dart';
import '../data/models/visit_dtos.dart';

/// Visit + license service (ISM-3, ISM-4, ISM-5).
class VisitService {
  VisitService._();
  static final instance = VisitService._();
  final _api = ApiClient.instance;

  /// GET /api/mobile/visits/inspection-types?inspectorId=...
  Future<List<InspectionType>> fetchInspectionTypes(int inspectorId) async {
    final body = await _api.get(
      '/api/mobile/visits/inspection-types',
      query: {'inspectorId': inspectorId},
    );
    return _asList(body).map((e) => InspectionType.fromJson(_asMap(e))).toList();
  }

  /// POST /api/mobile/visits/verify-license
  Future<LicenseVerificationResponse> verifyLicense({
    required String licenseNumber,
    String entryMethod = 'LICENSE_NUMBER',
  }) async {
    final body = await _api.post(
      '/api/mobile/visits/verify-license',
      body: {'licenseNumber': licenseNumber, 'entryMethod': entryMethod},
    );
    return LicenseVerificationResponse.fromJson(_asMap(body));
  }

  /// POST /api/mobile/visits?inspectorId=...
  Future<CreateVisitResponse> createVisit(
    VisitCreateRequest request, {
    required int inspectorId,
  }) async {
    final body = await _api.post(
      '/api/mobile/visits',
      query: {'inspectorId': inspectorId},
      body: request.toJson(),
    );
    return CreateVisitResponse.fromJson(_asMap(body));
  }

  /// POST /api/mobile/visits/no-license?inspectorId=...
  Future<CreateVisitResponse> createNoLicenseVisit(
    VisitCreateRequest request, {
    required int inspectorId,
  }) async {
    final body = await _api.post(
      '/api/mobile/visits/no-license',
      query: {'inspectorId': inspectorId},
      body: request.toJson(),
    );
    return CreateVisitResponse.fromJson(_asMap(body));
  }

  /// POST /api/mobile/visits/facility (no-license map pin facility)
  Future<Map<String, dynamic>> createFacility(FacilityCreateRequest req) async {
    final body = await _api.post('/api/mobile/visits/facility', body: req.toJson());
    return _asMap(body);
  }

  Map<String, dynamic> _asMap(dynamic body) =>
      body is Map<String, dynamic> ? body : <String, dynamic>{};
  List _asList(dynamic body) => body is List ? body : const [];
}
