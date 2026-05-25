import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/localization/app_strings.dart';
import '../routes/app_routes.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';
import '../widgets/app_bottom_sheet.dart';
import '../widgets/common_buttons.dart';
import '../widgets/screen_scaffold.dart';

/// Step: Review & Submit. Shows the compliance summary and drill-down links,
/// then a confirmation sheet before the visit is submitted.
/// Reference: Sprint2 screenshots 15 & 16.
class ReviewSubmitScreen extends StatelessWidget {
  const ReviewSubmitScreen({super.key});

  // Mock summary — wire to a real summary endpoint when the API is ready.
  static const double _compliancePercent = 97.0;
  static const int _nonCompliantCount = 1;

  Future<void> _openConfirmSheet(BuildContext context) async {
    final s = AppStrings.of(context);
    final confirmed = await showAppBottomSheet<bool>(
      context: context,
      title: s.t('notice'),
      child: Column(
        children: [
          const SizedBox(height: 8),
          const _MedalIllustration(),
          const SizedBox(height: 20),
          Text(
            s.t('confirmBeforeSending'),
            textAlign: TextAlign.center,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
              fontSize: 15,
            ),
          ),
          const SizedBox(height: 4),
        ],
      ),
      footer: Row(
        children: [
          Expanded(
            child: SecondaryButton(
              label: s.t('backOff'),
              onPressed: () => Navigator.of(context).pop(false),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: PrimaryButton(
              label: s.t('submit'),
              onPressed: () => Navigator.of(context).pop(true),
            ),
          ),
        ],
      ),
    );

    if (confirmed == true && context.mounted) {
      // TODO: POST the assembled inspection payload to the visit API here.
      Navigator.of(context).pushNamed(Routes.visitSuccess);
    }
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final session = context.watch<SessionState>();

    return InspectionScreenScaffold(
      title: s.t('review'),
      insNumber: session.caseDisplayId,
      totalSteps: 7,
      currentStep: 7,
      footer: FooterNavBar(
        previousLabel: s.t('previous'),
        nextLabel: s.t('submit'),
        onPrevious: () => Navigator.of(context).maybePop(),
        onNext: () => _openConfirmSheet(context),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Compliance percentage card
          Container(
            padding: const EdgeInsets.all(18),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(AppTheme.cardRadius),
              boxShadow: AppTheme.cardShadow,
            ),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        s.t('compliancePercentage'),
                        style: const TextStyle(
                          color: AppTheme.textSecondary,
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(
                        '${_compliancePercent.toStringAsFixed(1)} %',
                        style: const TextStyle(
                          color: AppTheme.textPrimary,
                          fontSize: 30,
                          fontWeight: FontWeight.w800,
                        ),
                      ),
                    ],
                  ),
                ),
                _ComplianceDonut(
                  percent: _compliancePercent,
                  nonCompliantCount: _nonCompliantCount,
                  label: s.t('nonCompliantClause'),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // Drill-down rows
          _ReviewRow(
            label: s.t('facilityStatus'),
            onTap: () => Navigator.of(context).maybePop(),
          ),
          const SizedBox(height: 12),
          _ReviewRow(
            label: s.t('nonCompliantItems'),
            onTap: () => Navigator.of(context).pushNamed(Routes.complianceClauses),
          ),
          const SizedBox(height: 12),
          _ReviewRow(
            label: s.t('violatorsInfo'),
            onTap: () => Navigator.of(context).pushNamed(Routes.violatorsInfo),
          ),
          const SizedBox(height: 12),
          _ReviewRow(
            label: s.t('notesAndAttachments'),
            onTap: () => Navigator.of(context).pushNamed(Routes.notesAttachments),
          ),
        ],
      ),
    );
  }
}

class _ReviewRow extends StatelessWidget {
  final String label;
  final VoidCallback onTap;
  const _ReviewRow({required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 18),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(AppTheme.cardRadius),
          border: Border.all(color: AppTheme.border),
        ),
        child: Row(
          children: [
            Expanded(
              child: Text(
                label,
                style: const TextStyle(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.w600,
                  fontSize: 15,
                ),
              ),
            ),
            const Icon(Icons.arrow_forward, size: 20, color: AppTheme.textPrimary),
          ],
        ),
      ),
    );
  }
}

/// Donut ring showing compliance percentage with the non-compliant count
/// in its center.
class _ComplianceDonut extends StatelessWidget {
  final double percent;
  final int nonCompliantCount;
  final String label;

  const _ComplianceDonut({
    required this.percent,
    required this.nonCompliantCount,
    required this.label,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 86,
      height: 86,
      child: CustomPaint(
        painter: _DonutPainter(percent / 100),
        child: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                '$nonCompliantCount',
                style: const TextStyle(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.w800,
                  fontSize: 16,
                ),
              ),
              Text(
                label,
                textAlign: TextAlign.center,
                style: const TextStyle(
                  color: AppTheme.textSecondary,
                  fontWeight: FontWeight.w600,
                  fontSize: 8,
                  height: 1.2,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _DonutPainter extends CustomPainter {
  final double fraction; // 0..1 compliant
  _DonutPainter(this.fraction);

  @override
  void paint(Canvas canvas, Size size) {
    const stroke = 9.0;
    final rect = Offset.zero & size;
    final center = rect.center;
    final radius = (size.shortestSide - stroke) / 2;

    final track = Paint()
      ..color = const Color(0xFFE6E9E8)
      ..style = PaintingStyle.stroke
      ..strokeWidth = stroke;
    canvas.drawCircle(center, radius, track);

    final progress = Paint()
      ..color = AppTheme.primary
      ..style = PaintingStyle.stroke
      ..strokeWidth = stroke
      ..strokeCap = StrokeCap.round;
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      -math.pi / 2,
      2 * math.pi * fraction.clamp(0.0, 1.0),
      false,
      progress,
    );
  }

  @override
  bool shouldRepaint(covariant _DonutPainter old) => old.fraction != fraction;
}

/// Award-medal illustration for the confirmation sheet — composed from
/// Material icons (no bundled asset needed).
class _MedalIllustration extends StatelessWidget {
  const _MedalIllustration();

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 96,
      height: 96,
      child: Stack(
        alignment: Alignment.center,
        children: [
          const PositionedDirectional(
            bottom: 0,
            child: Icon(Icons.workspace_premium_rounded,
                size: 56, color: Color(0xFF4C7FE0)),
          ),
          Container(
            decoration: const BoxDecoration(
              color: Color(0xFFF6B73C),
              shape: BoxShape.circle,
            ),
            padding: const EdgeInsets.all(14),
            child: const Icon(Icons.star_rounded, size: 34, color: Colors.white),
          ),
        ],
      ),
    );
  }
}
