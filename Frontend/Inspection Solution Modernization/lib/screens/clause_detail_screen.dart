import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/api/api_exception.dart';
import '../core/localization/app_strings.dart';
import '../data/models/clause_dto.dart';
import '../data/models/clause_model.dart';
import '../data/models/violation_dto.dart';
import '../services/clause_service.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';
import '../utils/extensions.dart';
import '../widgets/common_buttons.dart';

class ClauseDetailScreen extends StatefulWidget {
  const ClauseDetailScreen({super.key});

  @override
  State<ClauseDetailScreen> createState() => _ClauseDetailScreenState();
}

class _ClauseDetailScreenState extends State<ClauseDetailScreen> {
  Future<List<ClauseDto>>? _future;
  final Map<int, ComplianceStatus> _status = {};
  final Map<int, String> _reasons = {};
  bool _saving = false;

  @override
  void initState() {
    super.initState();
    _future = ClauseService.instance.listClauses();
  }

  Future<void> _save() async {
    final session = context.read<SessionState>();
    final inspectionId = session.inspectionId;
    if (inspectionId == null) {
      context.showSnack('Start a visit first', color: AppTheme.warning);
      return;
    }
    final all = await _future ?? const <ClauseDto>[];
    if (!mounted) return;
    final nonCompliant = all.where(
      (c) => _status[c.id] == ComplianceStatus.nonCompliant,
    );
    if (nonCompliant.isEmpty) {
      context.showSnack('Nothing to save');
      return;
    }
    setState(() => _saving = true);
    try {
      for (final c in nonCompliant) {
        final reason = (_reasons[c.id] ?? '').trim();
        await ClauseService.instance.addViolation(
          inspectionId,
          CreateViolationRequest(
            clauseId: c.id,
            severity: (c.severity ?? 'LOW').toUpperCase(),
            violationDescription: reason.isEmpty ? c.title : reason,
          ),
        );
      }
      if (!mounted) return;
      context.showSnack('Violations saved');
      Navigator.of(context).popUntil((r) => r.isFirst);
    } on ApiException catch (e) {
      if (!mounted) return;
      context.showSnack(e.message, color: AppTheme.error);
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(60),
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 8),
            child: Row(
              children: [
                IconButton(
                  onPressed: () => Navigator.of(context).pop(),
                  icon: const Icon(Icons.chevron_left,
                      color: AppTheme.textPrimary, size: 28),
                ),
                Expanded(
                  child: Text(
                    s.t('clauseDetailTitle'),
                    textAlign: TextAlign.right,
                    style: const TextStyle(
                      color: AppTheme.textPrimary,
                      fontWeight: FontWeight.w700,
                      fontSize: 18,
                    ),
                  ),
                ),
                IconButton(
                  onPressed: () {},
                  icon: const Icon(Icons.search, color: AppTheme.textPrimary),
                ),
              ],
            ),
          ),
        ),
      ),
      body: FutureBuilder<List<ClauseDto>>(
        future: _future,
        builder: (_, snap) {
          if (snap.connectionState != ConnectionState.done) {
            return const Center(child: CircularProgressIndicator(color: AppTheme.primary));
          }
          if (snap.hasError) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.error_outline, color: AppTheme.error, size: 36),
                    const SizedBox(height: 12),
                    Text('${snap.error}',
                        textAlign: TextAlign.center,
                        style: const TextStyle(color: AppTheme.error)),
                  ],
                ),
              ),
            );
          }
          final clauses = snap.data ?? const <ClauseDto>[];
          return Column(
            children: [
              Padding(
                padding: const EdgeInsets.fromLTRB(16, 0, 16, 12),
                child: Row(
                  children: [
                    Text(
                      s.t('allClauses'),
                      style: const TextStyle(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w700,
                        fontSize: 16,
                      ),
                    ),
                    const SizedBox(width: 8),
                    Container(
                      padding:
                          const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
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
                    const Spacer(),
                    Container(
                      width: 38,
                      height: 38,
                      decoration: BoxDecoration(
                        color: Colors.white,
                        border: Border.all(color: AppTheme.border),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: const Icon(Icons.tune,
                          color: AppTheme.textPrimary, size: 18),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: clauses.isEmpty
                    ? const Center(child: Text('No clauses available'))
                    : ListView.separated(
                        padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
                        itemCount: clauses.length,
                        separatorBuilder: (_, _) => const SizedBox(height: 12),
                        itemBuilder: (_, i) {
                          final c = clauses[i];
                          return _ClauseDetailCard(
                            clause: c,
                            status: _status[c.id] ?? ComplianceStatus.none,
                            reason: _reasons[c.id] ?? '',
                            onChanged: (status) =>
                                setState(() => _status[c.id] = status),
                            onReasonChanged: (text) => _reasons[c.id] = text,
                          );
                        },
                      ),
              ),
              SafeArea(
                top: false,
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
                  child: PrimaryButton(
                    label: s.t('save'),
                    onPressed: _save,
                    loading: _saving,
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}

class _ClauseDetailCard extends StatefulWidget {
  final ClauseDto clause;
  final ComplianceStatus status;
  final String reason;
  final ValueChanged<ComplianceStatus> onChanged;
  final ValueChanged<String> onReasonChanged;

  const _ClauseDetailCard({
    required this.clause,
    required this.status,
    required this.reason,
    required this.onChanged,
    required this.onReasonChanged,
  });

  @override
  State<_ClauseDetailCard> createState() => _ClauseDetailCardState();
}

class _ClauseDetailCardState extends State<_ClauseDetailCard> {
  late final TextEditingController _reasonController;

  @override
  void initState() {
    super.initState();
    _reasonController = TextEditingController(text: widget.reason);
    _reasonController.addListener(() => widget.onReasonChanged(_reasonController.text));
  }

  @override
  void dispose() {
    _reasonController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final c = widget.clause;
    final title = s.isAr ? (c.titleAr ?? c.title) : c.title;
    final desc = s.isAr ? (c.descriptionAr ?? c.description) : c.description;
    final code = c.code.isEmpty ? 'Clause ${c.id}' : 'Clause ${c.code}';
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppTheme.cardRadius),
        boxShadow: AppTheme.cardShadow,
      ),
      padding: const EdgeInsets.all(14),
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
                  fontSize: 14,
                ),
              ),
              const Spacer(),
              _RiskBadge(risk: c.risk),
            ],
          ),
          const SizedBox(height: 6),
          Text(
            title,
            textAlign: TextAlign.right,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
              fontSize: 14,
            ),
          ),
          if (desc != null && desc.isNotEmpty) ...[
            const SizedBox(height: 4),
            Text(
              desc,
              textAlign: TextAlign.right,
              style: const TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 12,
                height: 1.4,
              ),
            ),
          ],
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(
                child: _OptionPill(
                  label: s.t('compliant'),
                  active: widget.status == ComplianceStatus.compliant,
                  color: AppTheme.success,
                  onTap: () => widget.onChanged(ComplianceStatus.compliant),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: _OptionPill(
                  label: s.t('nonCompliantOpt'),
                  active: widget.status == ComplianceStatus.nonCompliant,
                  color: const Color(0xFFE0931F),
                  onTap: () => widget.onChanged(ComplianceStatus.nonCompliant),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: _OptionPill(
                  label: s.t('notApplicable'),
                  active: widget.status == ComplianceStatus.notApplicable,
                  color: AppTheme.textPrimary,
                  onTap: () => widget.onChanged(ComplianceStatus.notApplicable),
                ),
              ),
            ],
          ),
          if (widget.status == ComplianceStatus.nonCompliant) ...[
            const SizedBox(height: 12),
            Container(
              decoration: BoxDecoration(
                border: Border.all(color: AppTheme.border),
                borderRadius: BorderRadius.circular(10),
                color: Colors.white,
              ),
              padding: const EdgeInsets.fromLTRB(12, 4, 6, 4),
              child: Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _reasonController,
                      decoration: InputDecoration(
                        hintText: s.t('enterReason'),
                        hintStyle: const TextStyle(
                          color: AppTheme.textSecondary,
                          fontSize: 13,
                        ),
                        border: InputBorder.none,
                        focusedBorder: InputBorder.none,
                        enabledBorder: InputBorder.none,
                      ),
                    ),
                  ),
                  Container(
                    width: 36,
                    height: 36,
                    decoration: BoxDecoration(
                      color: AppTheme.primary,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: const Icon(Icons.arrow_forward,
                        color: Colors.white, size: 18),
                  ),
                ],
              ),
            ),
          ],
        ],
      ),
    );
  }
}

class _OptionPill extends StatelessWidget {
  final String label;
  final bool active;
  final Color color;
  final VoidCallback onTap;
  const _OptionPill({
    required this.label,
    required this.active,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        padding: const EdgeInsets.symmetric(vertical: 10),
        alignment: Alignment.center,
        decoration: BoxDecoration(
          color: active ? color.withValues(alpha: 0.12) : Colors.white,
          border: Border.all(color: active ? color : AppTheme.border),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Text(
          label,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: TextStyle(
            color: active ? color : AppTheme.textPrimary,
            fontWeight: FontWeight.w600,
            fontSize: 13,
          ),
        ),
      ),
    );
  }
}

class _RiskBadge extends StatelessWidget {
  final ClauseRisk risk;
  const _RiskBadge({required this.risk});

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    String label;
    Color color;
    switch (risk) {
      case ClauseRisk.low:
        label = s.t('low');
        color = AppTheme.success;
        break;
      case ClauseRisk.medium:
        label = s.t('medium');
        color = AppTheme.warning;
        break;
      case ClauseRisk.high:
        label = s.t('high');
        color = AppTheme.error;
        break;
    }
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border.all(color: color),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        label,
        style: TextStyle(
          color: color,
          fontWeight: FontWeight.w700,
          fontSize: 11,
        ),
      ),
    );
  }
}
