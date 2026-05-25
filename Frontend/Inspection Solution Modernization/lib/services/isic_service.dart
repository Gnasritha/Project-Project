import '../core/api/api_client.dart';
import '../data/models/isic_models.dart';

class IsicService {
  IsicService._();
  static final instance = IsicService._();
  final _api = ApiClient.instance;

  /// GET /api/mobile/isic-activities
  Future<List<IsicActivity>> fetchTopLevel() async {
    final body = await _api.get('/api/mobile/isic-activities');
    return (body is List ? body : const [])
        .map((e) => IsicActivity.fromJson(e is Map<String, dynamic> ? e : <String, dynamic>{}))
        .toList();
  }

  /// GET /api/mobile/isic-activities/{id}/details
  Future<List<IsicDetailActivity>> fetchDetails(int categoryId) async {
    final body = await _api.get('/api/mobile/isic-activities/$categoryId/details');
    return (body is List ? body : const [])
        .map((e) => IsicDetailActivity.fromJson(e is Map<String, dynamic> ? e : <String, dynamic>{}))
        .toList();
  }
}
