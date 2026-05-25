/// Form-level validators returning a localizable key/message or null when valid.
class Validators {
  Validators._();

  static String? required(String? v, {String message = 'This field is required'}) {
    if (v == null || v.trim().isEmpty) return message;
    return null;
  }

  static String? idOrMobile(String? v) {
    if (v == null || v.trim().isEmpty) return 'Enter ID number or mobile';
    final trimmed = v.trim();
    final isNumeric = RegExp(r'^[0-9+]+$').hasMatch(trimmed);
    if (!isNumeric) return 'Only numbers allowed';
    if (trimmed.length < 8) return 'Must be at least 8 digits';
    return null;
  }

  static String? password(String? v) {
    if (v == null || v.isEmpty) return 'Enter your password';
    return null;
  }

  static String? licenseNumber(String? v) {
    if (v == null || v.trim().isEmpty) return 'Enter license number';
    if (v.trim().length < 6) return 'Invalid license number';
    return null;
  }
}
