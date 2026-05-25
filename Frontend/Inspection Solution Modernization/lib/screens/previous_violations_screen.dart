import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../core/localization/app_strings.dart';
import '../data/models/violation_dto.dart';
import '../routes/app_routes.dart';
import '../services/clause_service.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';
import '../widgets/common_buttons.dart';
import '../widgets/screen_scaffold.dart';

class PreviousViolationsScreen extends StatefulWidget {
  const PreviousViolationsScreen({super.key});

  @override
  State<PreviousViolationsScreen> createState() => _PreviousViolationsScreenState();
}

class _PreviousViolationsScreenState extends State<PreviousViolationsScreen> {
  Future<List<ViolationDto>>? _future;

  @override
  void initState() {
    super.initState();
    final session = context.read<SessionState>();
    final license = session.licenseNumber;
    if (license != null && license.isNotEmpty) {
      _future = ClauseService.instance.previousViolations(
        license,
        excludeInspectionId: session.inspectionId,
      );
    } else {
      _future = Future.value(const []);
    }
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final session = context.watch<SessionState>();
    return InspectionScreenScaffold(
      title: s.t('previousViolations'),
      insNumber: session.caseDisplayId,
      totalSteps: 6,
      currentStep: 3,
      footer: FooterNavBar(
        previousLabel: s.t('previous'),
        nextLabel: s.t('next'),
        onPrevious: () => Navigator.of(context).pop(),
        onNext: () => Navigator.of(context).pushNamed(Routes.complianceClauses),
      ),
      child: FutureBuilder<List<ViolationDto>>(
        future: _future,
        builder: (_, snapshot) {
          if (snapshot.connectionState != ConnectionState.done) {
            return const Padding(
              padding: EdgeInsets.symmetric(vertical: 32),
              child: Center(child: CircularProgressIndicator(color: AppTheme.primary)),
            );
          }
          if (snapshot.hasError) {
            return _ErrorState(message: '${snapshot.error}');
          }
          final list = snapshot.data ?? const <ViolationDto>[];
          if (list.isEmpty) {
            return const _EmptyState();
          }
          return Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: list
                .map((v) => Padding(
                      padding: const EdgeInsets.only(bottom: 12),
                      child: _ViolationCard(violation: v),
                    ))
                .toList(),
          );
        },
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  const _EmptyState();
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 64),
      child: Column(
        children: [
          const Icon(Icons.fact_check_outlined,
              color: AppTheme.textSecondary, size: 56),
          const SizedBox(height: 12),
          Text(
            'No previous violations found',
            style: TextStyle(
              color: AppTheme.textSecondary.withValues(alpha: 0.9),
              fontSize: 14,
            ),
          ),
        ],
      ),
    );
  }
}

class _ErrorState extends StatelessWidget {
  final String message;
  const _ErrorState({required this.message});
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 32),
      child: Column(
        children: [
          const Icon(Icons.error_outline, color: AppTheme.error, size: 32),
          const SizedBox(height: 12),
          Text(message,
              textAlign: TextAlign.center,
              style: const TextStyle(color: AppTheme.error, fontSize: 13)),
        ],
      ),
    );
  }
}

class _ViolationCard extends StatelessWidget {
  final ViolationDto violation;
  const _ViolationCard({required this.violation});

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final isFixed = (violation.status ?? '').toUpperCase() == 'FIXED';
    final dateLabel = violation.violationDate != null
        ? DateFormat('d MMMM, y').format(violation.violationDate!)
        : '—';
    final title = violation.clauseTitleAr ?? violation.clauseTitle ?? '';
    final desc = violation.violationDescription ?? '';
    final code = violation.clauseCode ?? 'V-${violation.violationId}';

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppTheme.cardRadius),
        boxShadow: AppTheme.cardShadow,
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(
                code,
                style: const TextStyle(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.w700,
                  fontSize: 16,
                ),
              ),
              const Spacer(),
              _StatusPill(
                label: isFixed ? s.t('fixed') : s.t('needsDecision'),
                color: isFixed ? AppTheme.success : AppTheme.textPrimary,
                background: isFixed
                    ? AppTheme.success.withValues(alpha: 0.10)
                    : const Color(0xFFF1F1F1),
                border: isFixed ? AppTheme.success : Colors.transparent,
              ),
            ],
          ),
          if (title.isNotEmpty) ...[
            const SizedBox(height: 14),
            Text(
              title,
              textAlign: TextAlign.right,
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w600,
                fontSize: 14,
              ),
            ),
          ],
          if (desc.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(
              desc,
              textAlign: TextAlign.right,
              style: const TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 13,
                height: 1.6,
              ),
            ),
          ],
          const SizedBox(height: 18),
          Align(
            alignment: AlignmentDirectional.centerEnd,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  s.t('violationDate'),
                  style: const TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  dateLabel,
                  style: const TextStyle(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w700,
                    fontSize: 14,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _StatusPill extends StatelessWidget {
  final String label;
  final Color color;
  final Color background;
  final Color border;

  const _StatusPill({
    required this.label,
    required this.color,
    required this.background,
    required this.border,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      decoration: BoxDecoration(
        color: background,
        border: Border.all(color: border),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        label,
        style: TextStyle(
          color: color,
          fontWeight: FontWeight.w600,
          fontSize: 12,
        ),
      ),
    );
  }
}
