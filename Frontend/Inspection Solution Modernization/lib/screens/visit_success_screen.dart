import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '../core/localization/app_strings.dart';
import '../routes/app_routes.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';
import '../utils/extensions.dart';
import '../widgets/app_header.dart';
import '../widgets/common_buttons.dart';

/// Final step of the inspection workflow — confirmation that the visit was
/// submitted successfully. Reference: Sprint2 screenshot 17.
class VisitSuccessScreen extends StatelessWidget {
  const VisitSuccessScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final visitNumber = context.watch<SessionState>().inspectionNumber ?? '—';

    return Scaffold(
      backgroundColor: AppTheme.background,
      body: Column(
        children: [
          const AppHeader(compact: true),
          Expanded(
            child: Center(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 32),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const _SuccessIllustration(),
                    const SizedBox(height: 28),
                    Text(
                      s.t('visitSentSuccessfully'),
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                        color: AppTheme.primaryDark,
                        fontWeight: FontWeight.w700,
                        fontSize: 16,
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 14),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          s.t('visitNumber'),
                          style: const TextStyle(
                            color: AppTheme.primary,
                            fontWeight: FontWeight.w700,
                            fontSize: 14,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Text(
                          visitNumber,
                          style: const TextStyle(
                            color: AppTheme.textPrimary,
                            fontWeight: FontWeight.w700,
                            fontSize: 14,
                          ),
                        ),
                        const SizedBox(width: 6),
                        GestureDetector(
                          onTap: () {
                            Clipboard.setData(ClipboardData(text: visitNumber));
                            context.showSnack(
                              s.t('dataSavedSuccessfully'),
                              color: AppTheme.success,
                            );
                          },
                          behavior: HitTestBehavior.opaque,
                          child: const Icon(Icons.copy_rounded,
                              size: 18, color: AppTheme.primary),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ),
          FooterNavBar(
            previousLabel: s.t('previous'),
            nextLabel: s.t('next'),
            onPrevious: () => Navigator.of(context).maybePop(),
            onNext: () => Navigator.of(context).pushNamedAndRemoveUntil(
              Routes.dashboard,
              (route) => false,
            ),
          ),
        ],
      ),
    );
  }
}

/// Composed "document + check + envelope" success illustration — built from
/// Material icons so it needs no bundled asset.
class _SuccessIllustration extends StatelessWidget {
  const _SuccessIllustration();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 132,
      height: 132,
      decoration: BoxDecoration(
        color: const Color(0xFFEDEFF1),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Stack(
        alignment: Alignment.center,
        children: [
          const Icon(Icons.description_rounded,
              size: 70, color: Color(0xFFB9C0C6)),
          PositionedDirectional(
            bottom: 30,
            end: 26,
            child: Container(
              decoration: const BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
              ),
              padding: const EdgeInsets.all(2),
              child: const Icon(Icons.check_circle,
                  size: 34, color: AppTheme.success),
            ),
          ),
          const PositionedDirectional(
            bottom: 24,
            start: 22,
            child: Icon(Icons.email_rounded, size: 40, color: Color(0xFFF6B73C)),
          ),
        ],
      ),
    );
  }
}
