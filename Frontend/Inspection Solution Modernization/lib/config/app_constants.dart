class AppConstants {
  AppConstants._();

  static const String appName = 'National Platform for Control and Inspection';
  static const String appNameAr = 'المنصة الوطنية للرقابة والتفتيش';

  // Backend (Momthathel) base URL — see Postman collection.
  // 10.0.2.2 is the Android emulator's alias for the host machine's loopback
  // (the Spring Boot server, server.port=8980 in application.yml). Use
  // 'localhost' only when running on the host itself (web/desktop builds).
  static const String baseUrl = 'http://10.0.2.2:8980';
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
