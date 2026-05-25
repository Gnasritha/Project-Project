import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/localization/app_strings.dart';
import '../services/locale_service.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';

/// Dark green hero header used at the top of dashboard, visits and inspection screens.
///
/// Reads the current inspector profile from [SessionState] — no hardcoded
/// user data. Falls back to graceful placeholders when the user has not
/// been loaded yet (e.g. before /api/auth/me succeeds).
class AppHeader extends StatelessWidget {
  final bool compact;

  const AppHeader({super.key, this.compact = false});

  String _greetingFor(int hour, AppStrings s) {
    if (hour < 12) return s.t('goodMorning');
    if (hour < 18) return s.t('goodAfternoon');
    return s.t('goodEvening');
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final locale = context.watch<LocaleService>();
    final user = context.watch<SessionState>().user;

    final displayName = (s.isAr ? user?.fullNameAr : user?.fullName) ??
        user?.username ??
        '';
    final role = user?.role ?? s.t('fieldInspector');
    final greeting = _greetingFor(DateTime.now().hour, s);
    final avatarUrl = user?.avatarUrl;

    final padding = compact
        ? const EdgeInsets.fromLTRB(16, 18, 16, 14)
        : const EdgeInsets.fromLTRB(16, 24, 16, 18);

    return Container(
      width: double.infinity,
      decoration: const BoxDecoration(gradient: AppTheme.headerGradient),
      padding: padding,
      child: SafeArea(
        bottom: false,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                _Avatar(url: avatarUrl, name: displayName),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        displayName.isEmpty ? '—' : displayName,
                        style: const TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w700,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(
                            role,
                            style: const TextStyle(
                              color: Colors.white70,
                              fontSize: 12,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                          const Icon(Icons.keyboard_arrow_down,
                              size: 16, color: Colors.white70),
                        ],
                      ),
                    ],
                  ),
                ),
                _LocaleSwitch(
                  isAr: locale.isArabic,
                  onTap: () => locale.toggle(),
                ),
              ],
            ),
            SizedBox(height: compact ? 8 : 14),
            Text(
              greeting,
              style: const TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.w700,
                fontSize: 18,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              s.t('platformTagline'),
              style: const TextStyle(
                color: Colors.white70,
                fontSize: 12,
                fontWeight: FontWeight.w500,
                height: 1.4,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _Avatar extends StatelessWidget {
  final String? url;
  final String name;
  const _Avatar({required this.url, required this.name});

  @override
  Widget build(BuildContext context) {
    final initial = name.isNotEmpty ? name.characters.first.toUpperCase() : '?';
    return Container(
      width: 44,
      height: 44,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: Colors.white.withValues(alpha: 0.20),
        border: Border.all(color: Colors.white, width: 2),
        image: (url != null && url!.isNotEmpty)
            ? DecorationImage(fit: BoxFit.cover, image: NetworkImage(url!))
            : null,
      ),
      alignment: Alignment.center,
      child: (url == null || url!.isEmpty)
          ? Text(
              initial,
              style: const TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.w700,
                fontSize: 18,
              ),
            )
          : null,
    );
  }
}

class _LocaleSwitch extends StatelessWidget {
  final bool isAr;
  final VoidCallback onTap;
  const _LocaleSwitch({required this.isAr, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
        decoration: BoxDecoration(
          color: Colors.white.withValues(alpha: 0.15),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              isAr ? 'EN' : 'AR',
              style: const TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.w700,
                fontSize: 12,
              ),
            ),
            const SizedBox(width: 6),
            Container(
              width: 28,
              height: 18,
              decoration: BoxDecoration(
                color: const Color(0xFF006C35),
                borderRadius: BorderRadius.circular(3),
              ),
              alignment: Alignment.center,
              child: const Text(
                'KSA',
                style: TextStyle(
                    color: Colors.white,
                    fontSize: 8,
                    fontWeight: FontWeight.w700),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Header used on splash & login (light gradient + locale switch).
class LightLogoHeader extends StatelessWidget {
  const LightLogoHeader({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      bottom: false,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 8),
        child: Row(
          children: [
            const Spacer(),
            Consumer<LocaleService>(
              builder: (_, loc, _) => GestureDetector(
                onTap: loc.toggle,
                child: Row(
                  children: [
                    Text(
                      loc.isArabic ? 'EN' : 'AR',
                      style: const TextStyle(
                        color: AppTheme.primary,
                        fontWeight: FontWeight.w700,
                        fontSize: 14,
                      ),
                    ),
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 4),
                      decoration: BoxDecoration(
                        color: const Color(0xFF006C35),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: const Text(
                        'KSA',
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w700,
                          fontSize: 10,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Localization-aware text helper.
class T extends StatelessWidget {
  final String tkey;
  final TextStyle? style;
  final TextAlign? align;
  const T(this.tkey, {super.key, this.style, this.align});

  @override
  Widget build(BuildContext context) {
    return Text(
      AppStrings.of(context).t(tkey),
      style: style,
      textAlign: align,
    );
  }
}
