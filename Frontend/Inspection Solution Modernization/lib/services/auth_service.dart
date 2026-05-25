import '../config/app_constants.dart';
import '../core/api/api_client.dart';
import '../data/models/auth_models.dart';
import 'storage_service.dart';

/// Authentication service backed by Momthathel auth APIs (ISM-2).
class AuthService {
  AuthService._();
  static final instance = AuthService._();

  final _api = ApiClient.instance;

  /// POST /api/auth/login  body: {username, password, language}
  Future<LoginResponse> login({
    required String username,
    required String password,
    String language = 'en',
  }) async {
    final body = await _api.post(
      '/api/auth/login',
      auth: false,
      body: {
        'username': username,
        'password': password,
        'language': language,
      },
    );
    final res = LoginResponse.fromJson(_asMap(body));
    if (res.authToken.isNotEmpty) {
      await StorageService.instance
          .setString(AppConstants.kAuthToken, res.authToken);
    }
    final uname = res.username ?? username;
    if (uname.isNotEmpty) {
      await StorageService.instance.setString(AppConstants.kUserName, uname);
    }
    return res;
  }

  /// GET /api/auth/me
  Future<CurrentUser> me() async {
    final body = await _api.get('/api/auth/me');
    return CurrentUser.fromJson(_asMap(body));
  }

  /// POST /api/auth/logout
  Future<void> logout() async {
    try {
      await _api.post('/api/auth/logout');
    } finally {
      await StorageService.instance.remove(AppConstants.kAuthToken);
    }
  }

  bool get isLoggedIn =>
      (StorageService.instance.getString(AppConstants.kAuthToken) ?? '').isNotEmpty;

  String? get userName => StorageService.instance.getString(AppConstants.kUserName);

  Map<String, dynamic> _asMap(dynamic body) =>
      body is Map<String, dynamic> ? body : <String, dynamic>{};
}
