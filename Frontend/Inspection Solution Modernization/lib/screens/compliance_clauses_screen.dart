import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/localization/app_strings.dart';
import '../data/models/clause_dto.dart';
import '../routes/app_routes.dart';
import '../services/clause_service.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';
import '../widgets/common_buttons.dart';
import '../widgets/non_compliance_sheet.dart';
import '../widgets/screen_scaffold.dart';

class ComplianceClausesScreen extends StatefulWidget {
  const ComplianceClausesScreen({super.key});

  @override
  State<ComplianceClausesScreen> createState() => _ComplianceClausesScreenState();
}

class _ComplianceClausesScreenState extends State<ComplianceClausesScreen> {
  Future<Map<String, List<ClauseDto>>>? _future;
  final Set<String> _expanded = {};

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _future ??= _loadGrouped(AppStrings.of(context).isAr);
  }

  Future<Map<String, List<ClauseDto>>> _loadGrouped(bool isAr) async {
    final all = await ClauseService.instance.listClauses();
    final grouped = <String, List<ClauseDto>>{};
    for (final c in all) {
      final key = (isAr ? c.categoryAr : c.category) ?? 'General clauses';
      grouped.putIfAbsent(key, () => []).add(c);
    }
    if (grouped.isNotEmpty) _expanded.add(grouped.keys.first);
    return grouped;
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final session = context.watch<SessionState>();
    return InspectionScreenScaffold(
      title: s.t('complianceClauses'),
      insNumber: session.caseDisplayId,
      totalSteps: 6,
      currentStep: 4,
      footer: FooterNavBar(
        previousLabel: s.t('previous'),
        nextLabel: s.t('next'),
        onPrevious: () => Navigator.of(context).pop(),
        onNext: () => Navigator.of(context).pushNamed(Routes.violatorsInfo),
      ),
      child: FutureBuilder<Map<String, List<ClauseDto>>>(
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
          final groups = snapshot.data ?? const <String, List<ClauseDto>>{};
          if (groups.isEmpty) {
            return const Padding(
              padding: EdgeInsets.symmetric(vertical: 64),
              child: Center(child: Text('No clauses available')),
            );
          }
          return Column(
            children: groups.entries
                .map((e) => _GroupCard(
                      title: e.key,
                      clauses: e.value,
                      expanded: _expanded.contains(e.key),
                      onToggle: () => setState(() {
                        if (_expanded.contains(e.key)) {
                          _expanded.remove(e.key);
                        } else {
                          _expanded.add(e.key);
                        }
                      }),
                    ))
                .toList(),
          );
        },
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

class _GroupCard extends StatelessWidget {
  final String title;
  final List<ClauseDto> clauses;
  final bool expanded;
  final VoidCallback onToggle;

  const _GroupCard({
    required this.title,
    required this.clauses,
    required this.expanded,
    required this.onToggle,
  });

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppTheme.cardRadius),
        boxShadow: AppTheme.cardShadow,
      ),
      padding: const EdgeInsets.all(14),
      child: Column(
        children: [
          GestureDetector(
            onTap: onToggle,
            behavior: HitTestBehavior.opaque,
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Flexible(
                            child: Text(
                              title,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              style: const TextStyle(
                                color: AppTheme.textPrimary,
                                fontWeight: FontWeight.w700,
                                fontSize: 16,
                              ),
                            ),
                          ),
                          const SizedBox(width: 10),
                          Container(
                            width: 28,
                            height: 28,
                            alignment: Alignment.center,
                            decoration: const BoxDecoration(
                              color: AppTheme.primary,
                              shape: BoxShape.circle,
                            ),
                            child: Text(
                              '${clauses.length}',
                              style: const TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.w700,
                                fontSize: 12,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                AnimatedRotation(
                  duration: const Duration(milliseconds: 220),
                  turns: expanded ? 0.5 : 0,
                  child: const Icon(Icons.keyboard_arrow_down,
                      color: AppTheme.textPrimary),
                ),
              ],
            ),
          ),
          AnimatedSize(
            duration: const Duration(milliseconds: 220),
            curve: Curves.easeInOut,
            child: expanded
                ? Padding(
                    padding: const EdgeInsets.only(top: 12),
                    child: Column(
                      children: clauses
                          .take(8)
                          .map((c) => Padding(
                                padding: const EdgeInsets.only(bottom: 10),
                                child: _ClauseRow(
                                  clause: c,
                                  status: s.t('allCompliant'),
                                  onTap: () => showNonComplianceSheet(
                                    context,
                                    clause: s.isAr
                                        ? (c.titleAr ?? c.title)
                                        : c.title,
                                  ),
                                ),
                              ))
                          .toList(),
                    ),
                  )
                : const SizedBox.shrink(),
          ),
        ],
      ),
    );
  }
}

class _ClauseRow extends StatelessWidget {
  final ClauseDto clause;
  final String status;
  final VoidCallback? onTap;
  const _ClauseRow({required this.clause, required this.status, this.onTap});

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final title = s.isAr ? (clause.titleAr ?? clause.title) : clause.title;
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Container(
        decoration: BoxDecoration(
          border: Border.all(color: AppTheme.border),
          borderRadius: BorderRadius.circular(12),
        ),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        child: Row(
        children: [
          Container(
            width: 28,
            height: 28,
            alignment: Alignment.center,
            decoration: const BoxDecoration(
              color: Color(0xFFEFEFEF),
              shape: BoxShape.circle,
            ),
            child: Text(
              clause.code.isEmpty ? '${clause.id}' : clause.code,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w700,
                fontSize: 11,
              ),
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              title,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
              textAlign: TextAlign.right,
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w500,
                fontSize: 13,
              ),
            ),
          ),
          const SizedBox(width: 8),
          Flexible(
            child: Text(
              status,
              textAlign: TextAlign.right,
              maxLines: 2,
              style: const TextStyle(
                color: AppTheme.secondary,
                fontWeight: FontWeight.w600,
                fontSize: 13,
              ),
            ),
          ),
          const SizedBox(width: 6),
          const Icon(Icons.chevron_left,
              color: AppTheme.textSecondary, size: 18),
          ],
        ),
      ),
    );
  }
}
