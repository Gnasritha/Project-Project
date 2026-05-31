import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../config/app_constants.dart';
import '../core/api/api_exception.dart';
import '../core/localization/app_strings.dart';
import '../data/models/auth_models.dart';
import '../routes/app_routes.dart';
import '../services/auth_service.dart';
import '../services/locale_service.dart';
import '../services/session_state.dart';
import '../services/storage_service.dart';
import '../theme/app_theme.dart';
import '../utils/extensions.dart';
import '../utils/validators.dart';
import '../widgets/app_header.dart';
import '../widgets/app_logo.dart';
import '../widgets/common_buttons.dart';
import '../widgets/custom_text_field.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _userController = TextEditingController();
  final _passwordController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _rememberMe = false;
  bool _loading = false;

  @override
  void dispose() {
    _userController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _onLogin() async {
    if (!(_formKey.currentState?.validate() ?? false)) return;
    setState(() => _loading = true);
    final username = _userController.text.trim();
    try {
      final lang = context.read<LocaleService>().locale.languageCode;
      await AuthService.instance.login(
        username: username,
        password: _passwordController.text,
        language: lang,
      );
      await StorageService.instance
          .setBool(AppConstants.kRememberMe, _rememberMe);
      // Best-effort: pull /me for the inspector's real name. If the backend
      // doesn't expose it, fall back to the identity entered at login so the
      // dashboard header still reflects who signed in (instead of a blank).
      try {
        final me = await AuthService.instance.me();
        if (mounted) context.read<SessionState>().setUser(me);
      } catch (_) {
        if (mounted) {
          context.read<SessionState>().setUser(CurrentUser(
                id: AppConstants.defaultInspectorId,
                username: username,
                fullName: username,
              ));
        }
      }
      if (!mounted) return;
      Navigator.of(context).pushReplacementNamed(Routes.dashboard);
    } on ApiException catch (e) {
      if (!mounted) return;
      context.showSnack(e.message, color: AppTheme.error);
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _onNafath() async {
    // Demo entry point — Keycloak is not running in local dev, so this seeds
    // a placeholder inspector profile so the full UI flow can be walked
    // through without hitting /api/auth/*. The backend's `local` profile
    // opens /api/mobile/** so every screen and creation flow still works.
    // Remove this branch the moment a real /api/auth/nafath is added.
    setState(() => _loading = true);
    // Use the identity entered in the login field so the header shows the
    // name we logged in with; fall back to a generic demo label when blank.
    final entered = _userController.text.trim();
    final displayName = entered.isNotEmpty ? entered : 'Demo Inspector';
    await StorageService.instance.setString(AppConstants.kAuthToken, 'demo-nafath-token');
    await StorageService.instance.setString(AppConstants.kUserName, displayName);
    if (!mounted) return;
    context.read<SessionState>().setUser(CurrentUser(
          id: AppConstants.defaultInspectorId,
          username: entered.isNotEmpty ? entered : 'demo-inspector',
          fullName: displayName,
          fullNameAr: entered.isNotEmpty ? entered : 'مفتش تجريبي',
          role: 'Field Inspector (Demo)',
        ));
    setState(() => _loading = false);
    Navigator.of(context).pushReplacementNamed(Routes.dashboard);
  }

  void _onCreateAccount() {
    // Backend does not expose a self-service registration endpoint yet.
    context.showSnack(
      'Account creation is handled by your administrator',
      color: AppTheme.warning,
    );
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: SafeArea(
        child: Column(
          children: [
            const LightLogoHeader(),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(20, 4, 20, 24),
                child: Form(
                  key: _formKey,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const SizedBox(height: 4),
                      Center(
                        child: AppLogo(
                          size: 70,
                          color: AppTheme.textPrimary,
                          accentColor: AppTheme.primary,
                          showSubtitle: false,
                        ),
                      ),
                      const SizedBox(height: 16),
                      Text(
                        s.t('loginTitle'),
                        textAlign: TextAlign.center,
                        style: const TextStyle(
                          fontSize: 22,
                          fontWeight: FontWeight.w700,
                          color: AppTheme.textPrimary,
                          height: 1.3,
                        ),
                      ),
                      const SizedBox(height: 28),
                      CustomTextField(
                        label: s.t('idOrMobile'),
                        required: true,
                        hint: s.t('enterIdMobile'),
                        controller: _userController,
                        validator: Validators.required,
                      ),
                      const SizedBox(height: 16),
                      CustomTextField(
                        label: s.t('password'),
                        required: true,
                        hint: s.t('enterPassword'),
                        controller: _passwordController,
                        obscureText: true,
                        validator: Validators.password,
                      ),
                      const SizedBox(height: 8),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Row(
                            children: [
                              SizedBox(
                                width: 22,
                                height: 22,
                                child: Checkbox(
                                  value: _rememberMe,
                                  onChanged: (v) =>
                                      setState(() => _rememberMe = v ?? false),
                                  visualDensity: VisualDensity.compact,
                                  materialTapTargetSize:
                                      MaterialTapTargetSize.shrinkWrap,
                                ),
                              ),
                              const SizedBox(width: 8),
                              Text(
                                s.t('rememberMe'),
                                style: const TextStyle(
                                  color: AppTheme.textPrimary,
                                  fontSize: 14,
                                ),
                              ),
                            ],
                          ),
                          GestureDetector(
                            onTap: () => context.showSnack(
                              'Password reset is handled by your administrator',
                              color: AppTheme.warning,
                            ),
                            child: Text(
                              s.t('forgotPassword'),
                              style: const TextStyle(
                                color: AppTheme.textPrimary,
                                fontSize: 14,
                                fontWeight: FontWeight.w600,
                                decoration: TextDecoration.underline,
                              ),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 18),
                      PrimaryButton(
                        label: s.t('login'),
                        onPressed: _onLogin,
                        loading: _loading,
                      ),
                      const SizedBox(height: 14),
                      Center(
                        child: Text(
                          s.t('or'),
                          style: const TextStyle(
                            color: AppTheme.textSecondary,
                            fontWeight: FontWeight.w500,
                            fontSize: 14,
                          ),
                        ),
                      ),
                      const SizedBox(height: 14),
                      PrimaryButton(label: s.t('loginNafath'), onPressed: _onNafath),
                      const SizedBox(height: 18),
                      Center(
                        child: Wrap(
                          crossAxisAlignment: WrapCrossAlignment.center,
                          children: [
                            Text(
                              '${s.t('noAccount')} ',
                              style: const TextStyle(
                                color: AppTheme.textPrimary,
                                fontSize: 14,
                              ),
                            ),
                            InkWell(
                              onTap: _onCreateAccount,
                              borderRadius: BorderRadius.circular(4),
                              child: Padding(
                                padding: const EdgeInsets.symmetric(
                                    horizontal: 4, vertical: 2),
                                child: Text(
                                  s.t('createAccount'),
                                  style: const TextStyle(
                                    color: AppTheme.primary,
                                    fontWeight: FontWeight.w700,
                                    fontSize: 14,
                                    decoration: TextDecoration.underline,
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
