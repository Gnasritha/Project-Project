import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/localization/app_strings.dart';
import '../data/models/violator.dart';
import '../routes/app_routes.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';
import '../widgets/common_buttons.dart';
import '../widgets/screen_scaffold.dart';
import '../widgets/violator_data_sheet.dart';

/// Step: Violators Info. Lists violators attached to the case with an
/// identified/not-identified toggle and a data bottom sheet, plus the
/// establishment owner card. Reference: Sprint2 screenshots 7-12.
class ViolatorsInfoScreen extends StatefulWidget {
  const ViolatorsInfoScreen({super.key});

  @override
  State<ViolatorsInfoScreen> createState() => _ViolatorsInfoScreenState();
}

class _ViolatorsInfoScreenState extends State<ViolatorsInfoScreen> {
  // Seeded from the violation flow; "Add Violator" appends more.
  final List<Violator> _violators = [];
  bool _seeded = false;
  bool _autoSheetShown = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (_seeded) return;
    _seeded = true;

    // The establishment was looked up in the registry during the license
    // step; reuse that result instead of re-querying. found == true means the
    // violator/establishment exists in the database, so we auto-identify it.
    final info = context.read<SessionState>().licenseInfo;
    final identified = info?.found ?? false;

    _violators.add(Violator(
      name: AppStrings.of(context).t('contractor'),
      category: ViolatorCategory.entity,
      violationClauseCount: 1,
      // AC2: when the violator is on record, "Yes, identified" is preselected
      // and its registry details are filled in without inspector input.
      identified: identified ? true : null,
      nationalFacilityNumber: identified ? info?.nationalFacilityNumber : null,
      verification:
          identified ? VerificationState.verified : VerificationState.idle,
      dataFilled: identified,
    ));

    // Only drop the inspector straight into the "Fill in violator data" sheet
    // when the violator could NOT be auto-identified — a known violator
    // already has its details populated from the registry.
    if (!identified) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (!mounted || _autoSheetShown || _violators.isEmpty) return;
        _autoSheetShown = true;
        _fillData(_violators.first);
      });
    }
  }

  /// Establishment owner shown beneath the violator list. Built from the
  /// license-verification result already in the session (fetched from the
  /// registry), falling back to null when this inspection has no license
  /// context (the no-license flow), in which case the card is hidden.
  OwnerInfo? _ownerFrom(SessionState session) {
    final info = session.licenseInfo;
    if (info == null || !info.found) return null;
    final nameAr = info.establishmentNameAr;
    final name = (nameAr != null && nameAr.isNotEmpty)
        ? nameAr
        : (info.establishmentName ?? AppStrings.of(context).t('owner'));
    final facility = info.nationalFacilityNumber;
    return OwnerInfo(
      name: name,
      nationalFacilityNumber:
          (facility != null && facility.isNotEmpty) ? facility : '—',
      mobileNumber: info.mobileNumber,
    );
  }

  void _addViolator() {
    final s = AppStrings.of(context);
    setState(() {
      _violators.add(Violator(name: '${s.t('violator')} ${_violators.length + 1}'));
    });
  }

  Future<void> _fillData(Violator v) async {
    final updated = await showViolatorDataSheet(context, violator: v.copy());
    if (updated != null) {
      setState(() {
        final i = _violators.indexOf(v);
        if (i != -1) _violators[i] = updated;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final session = context.watch<SessionState>();
    final owner = _ownerFrom(session);

    return InspectionScreenScaffold(
      title: s.t('violatorsInfo'),
      insNumber: session.caseDisplayId,
      totalSteps: 7,
      currentStep: 5,
      footer: FooterNavBar(
        previousLabel: s.t('previous'),
        nextLabel: s.t('next'),
        onPrevious: () => Navigator.of(context).maybePop(),
        onNext: () => Navigator.of(context).pushNamed(Routes.notesAttachments),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SecondaryButton(
            label: s.t('addViolator'),
            icon: Icons.add,
            onPressed: _addViolator,
          ),
          const SizedBox(height: 16),
          for (final v in _violators) ...[
            _ViolatorCard(
              violator: v,
              onIdentifiedChanged: (val) => setState(() => v.identified = val),
              onFillData: () => _fillData(v),
            ),
            const SizedBox(height: 14),
          ],
          if (owner != null) ...[
            const SizedBox(height: 2),
            _OwnerCard(owner: owner),
          ],
        ],
      ),
    );
  }
}

/// Expandable violator accordion card.
class _ViolatorCard extends StatefulWidget {
  final Violator violator;
  final ValueChanged<bool> onIdentifiedChanged;
  final VoidCallback onFillData;

  const _ViolatorCard({
    required this.violator,
    required this.onIdentifiedChanged,
    required this.onFillData,
  });

  @override
  State<_ViolatorCard> createState() => _ViolatorCardState();
}

class _ViolatorCardState extends State<_ViolatorCard> {
  bool _expanded = true;

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final v = widget.violator;

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppTheme.cardRadius),
        border: Border.all(color: AppTheme.border),
        boxShadow: AppTheme.cardShadow,
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          GestureDetector(
            onTap: () => setState(() => _expanded = !_expanded),
            behavior: HitTestBehavior.opaque,
            child: Row(
              children: [
                Container(
                  width: 26,
                  height: 26,
                  decoration: BoxDecoration(
                    color: v.dataFilled ? AppTheme.success : AppTheme.secondary,
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(Icons.check, color: Colors.white, size: 16),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        v.name,
                        style: const TextStyle(
                          color: AppTheme.textPrimary,
                          fontWeight: FontWeight.w700,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        '${v.violationClauseCount} ${s.t('violationClauses')}',
                        style: const TextStyle(
                          color: AppTheme.textSecondary,
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ),
                Icon(
                  _expanded
                      ? Icons.keyboard_arrow_up
                      : Icons.keyboard_arrow_down,
                  color: AppTheme.textPrimary,
                ),
              ],
            ),
          ),
          AnimatedCrossFade(
            duration: const Duration(milliseconds: 200),
            crossFadeState:
                _expanded ? CrossFadeState.showFirst : CrossFadeState.showSecond,
            firstChild: _expandedBody(s, v),
            secondChild: const SizedBox(width: double.infinity),
          ),
        ],
      ),
    );
  }

  Widget _expandedBody(AppStrings s, Violator v) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(height: 16),
        Text(
          s.t('isViolatorIdentified'),
          style: const TextStyle(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.w600,
            fontSize: 14,
          ),
        ),
        const SizedBox(height: 10),
        Row(
          children: [
            Expanded(
              child: StatusChip(
                label: s.t('yesIdentified'),
                active: v.identified == true,
                activeColor: AppTheme.secondary,
                onTap: () => widget.onIdentifiedChanged(true),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatusChip(
                label: s.t('noCouldntIdentify'),
                active: v.identified == false,
                activeColor: AppTheme.secondary,
                onTap: () => widget.onIdentifiedChanged(false),
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        _DataButton(
          label: v.dataFilled
              ? s.t('editViolatorData')
              : s.t('fillViolatorData'),
          filled: v.dataFilled,
          onTap: widget.onFillData,
        ),
      ],
    );
  }
}

/// Outlined "Fill / Edit Violator Data" button. When [filled] is true a
/// trailing arrow chip is shown (matching screenshot 12).
class _DataButton extends StatelessWidget {
  final String label;
  final bool filled;
  final VoidCallback onTap;
  const _DataButton({
    required this.label,
    required this.filled,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Container(
        height: 50,
        padding: const EdgeInsetsDirectional.only(start: 14, end: 6),
        decoration: BoxDecoration(
          color: Colors.white,
          border: Border.all(color: AppTheme.border),
          borderRadius: BorderRadius.circular(AppTheme.buttonRadius),
        ),
        child: Row(
          children: [
            if (!filled)
              const Icon(Icons.attach_file, size: 18, color: AppTheme.textPrimary),
            if (!filled) const SizedBox(width: 8),
            Expanded(
              child: Text(
                label,
                style: const TextStyle(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.w600,
                  fontSize: 14,
                ),
              ),
            ),
            if (filled)
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
    );
  }
}

/// Establishment owner summary card.
class _OwnerCard extends StatelessWidget {
  final OwnerInfo owner;
  const _OwnerCard({required this.owner});

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppTheme.cardRadius),
        border: Border.all(color: AppTheme.border),
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            s.t('owner'),
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 13,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 6),
          Text(
            owner.name,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w700,
              fontSize: 15,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: _OwnerField(
                  label: s.t('nationalFacilityNumber'),
                  value: owner.nationalFacilityNumber,
                ),
              ),
              Expanded(
                child: _OwnerField(
                  label: s.t('mobileNumber'),
                  value: owner.mobileNumber ?? 'N/A',
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _OwnerField extends StatelessWidget {
  final String label;
  final String value;
  const _OwnerField({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(color: AppTheme.textSecondary, fontSize: 12),
        ),
        const SizedBox(height: 4),
        Text(
          value,
          style: const TextStyle(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.w600,
            fontSize: 14,
          ),
        ),
      ],
    );
  }
}
