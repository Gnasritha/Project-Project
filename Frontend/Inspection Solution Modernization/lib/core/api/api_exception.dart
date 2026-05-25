/// Normalized API error surfaced to UI layers.
class ApiException implements Exception {
  final int? statusCode;
  final String message;
  final dynamic body;

  ApiException(this.message, {this.statusCode, this.body});

  bool get isNetwork => statusCode == null;
  bool get isUnauthorized => statusCode == 401;
  bool get isNotFound => statusCode == 404;

  @override
  String toString() => 'ApiException($statusCode): $message';
}
