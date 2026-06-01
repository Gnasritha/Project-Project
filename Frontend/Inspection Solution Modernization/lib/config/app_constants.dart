import 'package:flutter/foundation.dart' show kIsWeb;

class AppConstants {
  AppConstants._();

  static const String appName = 'National Platform for Control and Inspection';
  static const String appNameAr = 'المنصة الوطنية للرقابة والتفتيش';

  // Backend (Momthathel) base URL.
  // - Web: defaults to the backend directly on :8980. The backend controllers
  //   are @CrossOrigin("*"), so cross-origin calls from the Flutter dev server
  //   work without a reverse proxy. When the app IS served behind a proxy that
  //   forwards /api/** (same-origin deploy), build with an empty override:
  //     flutter run -d chrome --dart-define=API_BASE_URL=
  // - Android emulator: 10.0.2.2 is the alias for the host-machine loopback
  //   (the Spring Boot server, server.port=8980 in application.yml).
  static const String baseUrl = kIsWeb
      ? String.fromEnvironment('API_BASE_URL',
          defaultValue: 'http://localhost:8980')
      : 'http://10.0.2.2:8980';
  static const Duration apiTimeout = Duration(seconds: 30);

  // Default inspector context until profile pull is wired.
  static const int defaultInspectorId = 1;

  // Map config
  static const double mapValidationDistanceMeters = 200.0;
  static const double defaultLat = 24.7136; // Riyadh
  static const double defaultLng = 46.6753;

  // Local storage keys
  static const String kAuthToken = 'auth_token';
  static const String kUserName = 'user_name';
  static const String kRememberMe = 'remember_me';
  static const String kLocale = 'app_locale';
}
