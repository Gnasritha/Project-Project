import 'dart:convert';
import 'dart:io';

import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';

import '../../config/app_constants.dart';
import '../../services/storage_service.dart';
import 'api_exception.dart';

/// Singleton HTTP client for the Momthathel backend.
/// All service classes go through this so auth headers, timeouts and error
/// normalization stay in one place.
class ApiClient {
  ApiClient._();
  static final ApiClient instance = ApiClient._();

  final http.Client _http = http.Client();

  String get _baseUrl => AppConstants.baseUrl;
  String? get _token => StorageService.instance.getString(AppConstants.kAuthToken);

  Map<String, String> _headers({bool json = true, bool auth = true}) {
    final h = <String, String>{};
    if (json) h['Content-Type'] = 'application/json';
    if (auth && (_token ?? '').isNotEmpty) {
      h['Authorization'] = 'Bearer $_token';
    }
    return h;
  }

  Uri _uri(String path, [Map<String, dynamic>? query]) {
    final clean = path.startsWith('/') ? path.substring(1) : path;
    final base = Uri.parse('$_baseUrl/');
    final qp = query?.map((k, v) => MapEntry(k, v?.toString() ?? ''));
    return base.resolve(clean).replace(
          queryParameters: (qp != null && qp.isNotEmpty) ? qp : null,
        );
  }

  Future<dynamic> get(String path, {Map<String, dynamic>? query, bool auth = true}) async {
    return _send(() => _http.get(_uri(path, query), headers: _headers(json: false, auth: auth)));
  }

  Future<dynamic> post(String path, {Object? body, Map<String, dynamic>? query, bool auth = true}) async {
    return _send(() => _http.post(
          _uri(path, query),
          headers: _headers(auth: auth),
          body: body == null ? null : jsonEncode(body),
        ));
  }

  Future<dynamic> delete(String path, {Map<String, dynamic>? query, bool auth = true}) async {
    return _send(() => _http.delete(_uri(path, query), headers: _headers(json: false, auth: auth)));
  }

  /// Upload a file via multipart/form-data. [bytes] for web, [filePath] for native.
  Future<dynamic> uploadFile(
    String path, {
    String fieldName = 'file',
    String? filePath,
    List<int>? bytes,
    String? filename,
    String? mimeType,
  }) async {
    final request = http.MultipartRequest('POST', _uri(path));
    request.headers.addAll(_headers(json: false));
    if (filePath != null) {
      request.files.add(await http.MultipartFile.fromPath(
        fieldName,
        filePath,
        contentType: mimeType != null ? MediaType.parse(mimeType) : null,
      ));
    } else if (bytes != null) {
      request.files.add(http.MultipartFile.fromBytes(
        fieldName,
        bytes,
        filename: filename ?? 'upload.bin',
        contentType: mimeType != null ? MediaType.parse(mimeType) : null,
      ));
    } else {
      throw ArgumentError('uploadFile requires filePath or bytes');
    }

    return _send(() async {
      final streamed = await request.send().timeout(AppConstants.apiTimeout);
      return http.Response.fromStream(streamed);
    });
  }

  Future<dynamic> _send(Future<http.Response> Function() send) async {
    http.Response res;
    try {
      res = await send().timeout(AppConstants.apiTimeout);
    } on SocketException catch (e) {
      throw ApiException('Network unavailable: ${e.message}');
    } on Exception catch (e) {
      throw ApiException('Request failed: $e');
    }

    final status = res.statusCode;
    final ct = res.headers['content-type'] ?? '';
    dynamic decoded;
    if (res.bodyBytes.isNotEmpty && ct.contains('application/json')) {
      try {
        decoded = jsonDecode(utf8.decode(res.bodyBytes));
      } catch (_) {
        decoded = res.body;
      }
    } else {
      decoded = res.body;
    }

    if (status >= 200 && status < 300) return decoded;

    final msg = _extractMessage(decoded) ?? 'Request failed (HTTP $status)';
    throw ApiException(msg, statusCode: status, body: decoded);
  }

  String? _extractMessage(dynamic body) {
    if (body is Map) {
      for (final k in const ['message', 'error', 'detail', 'errorMessage']) {
        final v = body[k];
        if (v is String && v.isNotEmpty) return v;
      }
    }
    if (body is String && body.isNotEmpty && body.length < 240) return body;
    return null;
  }

  void close() => _http.close();
}
