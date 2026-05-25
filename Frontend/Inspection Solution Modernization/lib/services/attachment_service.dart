import '../core/api/api_client.dart';
import '../data/models/attachment_models.dart';

class AttachmentService {
  AttachmentService._();
  static final instance = AttachmentService._();
  final _api = ApiClient.instance;

  /// POST /api/mobile/visits/{inspectionId}/attachments  (multipart, field "file")
  Future<AttachmentDto> upload(
    int inspectionId, {
    String? filePath,
    List<int>? bytes,
    String? filename,
    String? mimeType,
  }) async {
    final body = await _api.uploadFile(
      '/api/mobile/visits/$inspectionId/attachments',
      filePath: filePath,
      bytes: bytes,
      filename: filename,
      mimeType: mimeType,
    );
    return AttachmentDto.fromJson(body is Map<String, dynamic> ? body : <String, dynamic>{});
  }

  /// GET /api/mobile/visits/{inspectionId}/attachments
  Future<List<AttachmentDto>> list(int inspectionId) async {
    final body = await _api.get('/api/mobile/visits/$inspectionId/attachments');
    return (body is List ? body : const [])
        .map((e) => AttachmentDto.fromJson(e is Map<String, dynamic> ? e : <String, dynamic>{}))
        .toList();
  }

  /// POST /api/mobile/visits/{inspectionId}/attachments/validate
  Future<AttachmentValidationResult> validate(int inspectionId) async {
    final body = await _api.post('/api/mobile/visits/$inspectionId/attachments/validate');
    return AttachmentValidationResult.fromJson(body is Map<String, dynamic> ? body : <String, dynamic>{});
  }

  /// DELETE /api/mobile/visits/{inspectionId}/attachments/{attachmentId}
  Future<void> delete(int inspectionId, int attachmentId) async {
    await _api.delete('/api/mobile/visits/$inspectionId/attachments/$attachmentId');
  }
}
