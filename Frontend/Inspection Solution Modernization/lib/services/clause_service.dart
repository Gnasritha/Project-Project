import '../core/api/api_client.dart';
import '../data/models/clause_dto.dart';
import '../data/models/violation_dto.dart';

class ClauseService {
  ClauseService._();
  static final instance = ClauseService._();
  final _api = ApiClient.instance;

  /// GET /api/mobile/clauses
  Future<List<ClauseDto>> listClauses() async {
    final body = await _api.get('/api/mobile/clauses');
    return (body is List ? body : const [])
        .map((e) => ClauseDto.fromJson(e is Map<String, dynamic> ? e : <String, dynamic>{}))
        .toList();
  }

  /// POST /api/mobile/visits/{inspectionId}/violations
  Future<ViolationDto> addViolation(int inspectionId, CreateViolationRequest req) async {
    final body = await _api.post(
      '/api/mobile/visits/$inspectionId/violations',
      body: req.toJson(),
    );
    return ViolationDto.fromJson(body is Map<String, dynamic> ? body : <String, dynamic>{});
  }

  /// GET /api/mobile/visits/{inspectionId}/violations
  Future<List<ViolationDto>> listInspectionViolations(int inspectionId) async {
    final body = await _api.get('/api/mobile/visits/$inspectionId/violations');
    return (body is List ? body : const [])
        .map((e) => ViolationDto.fromJson(e is Map<String, dynamic> ? e : <String, dynamic>{}))
        .toList();
  }

  /// GET /api/mobile/licenses/{licenseNumber}/previous-violations
  Future<List<ViolationDto>> previousViolations(
    String licenseNumber, {
    int? excludeInspectionId,
  }) async {
    final body = await _api.get(
      '/api/mobile/licenses/$licenseNumber/previous-violations',
      query: excludeInspectionId != null ? {'excludeInspectionId': excludeInspectionId} : null,
    );
    return (body is List ? body : const [])
        .map((e) => ViolationDto.fromJson(e is Map<String, dynamic> ? e : <String, dynamic>{}))
        .toList();
  }

  /// DELETE /api/mobile/visits/{inspectionId}/violations/{violationId}
  Future<void> deleteViolation(int inspectionId, int violationId) async {
    await _api.delete('/api/mobile/visits/$inspectionId/violations/$violationId');
  }
}
