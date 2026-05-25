import 'package:flutter/material.dart';
import '../config/app_constants.dart';
import 'storage_service.dart';

/// App-wide locale provider. Defaults to English; toggles to Arabic (RTL).
class LocaleService extends ChangeNotifier {
  Locale _locale = const Locale('en');
  Locale get locale => _locale;

  bool get isArabic => _locale.languageCode == 'ar';

  Future<void> load() async {
    final saved = StorageService.instance.getString(AppConstants.kLocale);
    if (saved == 'ar' || saved == 'en') {
      _locale = Locale(saved!);
      notifyListeners();
    }
  }

  Future<void> toggle() async {
    _locale = isArabic ? const Locale('en') : const Locale('ar');
    await StorageService.instance.setString(AppConstants.kLocale, _locale.languageCode);
    notifyListeners();
  }

  Future<void> setLocale(Locale locale) async {
    _locale = locale;
    await StorageService.instance.setString(AppConstants.kLocale, locale.languageCode);
    notifyListeners();
  }
}
