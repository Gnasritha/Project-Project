/// Response from POST /api/auth/login
class LoginResponse {
  final String authToken;
  final int? userId;
  final String? username;
  final String? language;

  const LoginResponse({
    required this.authToken,
    this.userId,
    this.username,
    this.language,
  });

  factory LoginResponse.fromJson(Map<String, dynamic> j) => LoginResponse(
        authToken: (j['authToken'] ?? j['token'] ?? '').toString(),
        userId: (j['userId'] ?? j['id']) as int?,
        username: j['username'] as String?,
        language: j['language'] as String?,
      );
}

/// Response from GET /api/auth/me
class CurrentUser {
  final int id;
  final String username;
  final String? fullName;
  final String? fullNameAr;
  final String? role;
  final String? avatarUrl;

  const CurrentUser({
    required this.id,
    required this.username,
    this.fullName,
    this.fullNameAr,
    this.role,
    this.avatarUrl,
  });

  factory CurrentUser.fromJson(Map<String, dynamic> j) => CurrentUser(
        id: (j['id'] ?? j['userId'] ?? 0) as int,
        username: (j['username'] ?? '').toString(),
        fullName: (j['fullName'] ?? j['name']) as String?,
        fullNameAr: j['fullNameAr'] as String?,
        role: j['role'] as String?,
        avatarUrl: j['avatarUrl'] as String?,
      );
}
