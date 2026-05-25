/// Attachment item from GET /api/mobile/visits/{id}/attachments
class AttachmentDto {
  final int attachmentId;
  final String filename;
  final String? mimeType;
  final int? sizeBytes;
  final String? url;
  final DateTime? uploadedAt;

  const AttachmentDto({
    required this.attachmentId,
    required this.filename,
    this.mimeType,
    this.sizeBytes,
    this.url,
    this.uploadedAt,
  });

  factory AttachmentDto.fromJson(Map<String, dynamic> j) => AttachmentDto(
        attachmentId: (j['attachmentId'] ?? j['id'] ?? 0) as int,
        filename: (j['filename'] ?? j['name'] ?? '').toString(),
        mimeType: (j['mimeType'] ?? j['contentType']) as String?,
        sizeBytes: (j['sizeBytes'] ?? j['size']) as int?,
        url: j['url'] as String?,
        uploadedAt: j['uploadedAt'] != null ? DateTime.tryParse(j['uploadedAt'].toString()) : null,
      );
}

/// Response from POST /api/mobile/visits/{id}/attachments/validate
class AttachmentValidationResult {
  final bool valid;
  final String? message;
  final List<String> missing;

  const AttachmentValidationResult({
    required this.valid,
    this.message,
    this.missing = const [],
  });

  factory AttachmentValidationResult.fromJson(Map<String, dynamic> j) =>
      AttachmentValidationResult(
        valid: (j['valid'] ?? j['ok'] ?? false) == true,
        message: j['message'] as String?,
        missing: (j['missing'] as List?)?.map((e) => e.toString()).toList() ?? const [],
      );
}
