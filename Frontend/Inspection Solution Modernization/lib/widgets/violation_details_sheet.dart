import 'package:flutter/material.dart';
import '../core/localization/app_strings.dart';
import '../data/models/non_compliance_models.dart';
import '../theme/app_theme.dart';
import 'app_bottom_sheet.dart';
import 'common_buttons.dart';
import 'custom_text_field.dart';
import 'searchable_picker_sheet.dart';

/// Opens the "Violations details" bottom sheet for [violation]. Returns the
/// completed [Violation] on Save, or null if dismissed.
/// Reference: Sprint2 screenshots 3 & 4.
Future<Violation?> showViolationDetailsSheet(
  BuildContext context, {
  required Violation violation,
}) {
  return showModalBottomSheet<Violation>(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (_) => _ViolationDetailsSheet(violation: violation),
  );
}

class _ViolationDetailsSheet extends StatefulWidget {
  final Violation violation;
  const _ViolationDetailsSheet({required this.violation});

  @override
  State<_ViolationDetailsSheet> createState() => _ViolationDetailsSheetState();
}

class _ViolationDetailsSheetState extends State<_ViolationDetailsSheet> {
  late final TextEditingController _code;
  late final TextEditingController _units;
  CatalogOption? _violator;
  late final Set<String> _penalties;
  late bool _hasConsequences;
  PresenceStatus? _presence;
  CatalogOption? _requiredAction;

  @override
  void initState() {
    super.initState();
    final v = widget.violation;
    _code = TextEditingController(text: v.code);
    _units = TextEditingController(text: v.unitCount.toString());
    _violator = v.violator;
    _penalties = {...v.penalties};
    _hasConsequences = v.hasConsequences;
    _presence = v.presenceStatus;
    _requiredAction = v.requiredAction;
  }

  @override
  void dispose() {
    _code.dispose();
    _units.dispose();
    super.dispose();
  }

  Future<void> _pickViolator() async {
    final s = AppStrings.of(context);
    final picked = await showSearchablePicker(
      context: context,
      title: s.t('violator'),
      options: MockViolationCatalog.violators,
      selected: _violator,
    );
    if (picked != null) setState(() => _violator = picked);
  }

  void _save() {
    final v = widget.violation;
    v.code = _code.text.trim();
    v.unitCount = int.tryParse(_units.text.trim()) ?? 1;
    v.violator = _violator;
    v.penalties = _penalties.toList();
    v.hasConsequences = _hasConsequences;
    v.presenceStatus = _hasConsequences ? null : _presence;
    v.requiredAction = _hasConsequences ? null : _requiredAction;
    Navigator.of(context).pop(v);
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return AppBottomSheet(
      title: s.t('violationsDetails'),
      footer: PrimaryButton(label: s.t('save'), onPressed: _save),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CustomTextField(
            label: s.t('violationCode'),
            controller: _code,
            readOnly: true,
          ),
          const SizedBox(height: 16),
          CustomTextField(
            label: '${s.t('numberOfUnits')}  ${s.t('numberOfUnitsHint')}',
            hint: s.t('enterValue'),
            controller: _units,
            keyboardType: TextInputType.number,
          ),
          const SizedBox(height: 16),
          _PickerField(
            label: s.t('violator'),
            value: _violator?.label(s.isAr),
            hint: s.t('select'),
            onTap: _pickViolator,
          ),
          const SizedBox(height: 18),

          // Subsequent penalties
          Text(
            s.t('subsequentPenalties'),
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w700,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 10),
          for (final p in MockViolationCatalog.subsequentPenalties)
            Padding(
              padding: const EdgeInsets.only(bottom: 10),
              child: _PenaltyRow(
                option: p,
                checked: _penalties.contains(p.id),
                showAddProduct:
                    MockViolationCatalog.penaltiesWithProduct.contains(p.id),
                onChanged: (v) => setState(() {
                  v ? _penalties.add(p.id) : _penalties.remove(p.id);
                }),
              ),
            ),
          const SizedBox(height: 8),

          // Consequences question
          Text(
            s.t('consequencesQuestion'),
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 6),
          Row(
            children: [
              Expanded(
                child: _RadioRow(
                  label: s.t('yesThereAre'),
                  selected: _hasConsequences,
                  onTap: () => setState(() => _hasConsequences = true),
                ),
              ),
              Expanded(
                child: _RadioRow(
                  label: s.t('noThereAreNot'),
                  selected: !_hasConsequences,
                  onTap: () => setState(() => _hasConsequences = false),
                ),
              ),
            ],
          ),

          // Consequence-free path: presence status + required action
          if (!_hasConsequences) ...[
            const SizedBox(height: 18),
            Text(
              s.t('violatorPresenceStatus'),
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w700,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            _RadioRow(
              label: s.t('presentCooperative'),
              selected: _presence == PresenceStatus.presentCooperative,
              onTap: () => setState(
                  () => _presence = PresenceStatus.presentCooperative),
            ),
            _RadioRow(
              label: s.t('presentUncooperative'),
              selected: _presence == PresenceStatus.presentUncooperative,
              onTap: () => setState(
                  () => _presence = PresenceStatus.presentUncooperative),
            ),
            _RadioRow(
              label: s.t('absent'),
              selected: _presence == PresenceStatus.absent,
              onTap: () => setState(() => _presence = PresenceStatus.absent),
            ),
            const SizedBox(height: 14),
            CustomDropdownField<CatalogOption>(
              label: s.t('requiredAction'),
              value: _requiredAction,
              hint: s.t('requiredAction'),
              items: MockViolationCatalog.requiredActions
                  .map((o) => DropdownMenuItem(value: o, child: Text(o.label(s.isAr))))
                  .toList(),
              onChanged: (o) => setState(() => _requiredAction = o),
            ),
          ],
        ],
      ),
    );
  }
}

/// Subsequent-penalty checkbox row, optionally with an "Add product" chip.
class _PenaltyRow extends StatelessWidget {
  final CatalogOption option;
  final bool checked;
  final bool showAddProduct;
  final ValueChanged<bool> onChanged;

  const _PenaltyRow({
    required this.option,
    required this.checked,
    required this.showAddProduct,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return GestureDetector(
      onTap: () => onChanged(!checked),
      behavior: HitTestBehavior.opaque,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
        decoration: BoxDecoration(
          color: Colors.white,
          border: Border.all(
            color: checked ? AppTheme.primary : AppTheme.border,
          ),
          borderRadius: BorderRadius.circular(AppTheme.inputRadius),
        ),
        child: Row(
          children: [
            SizedBox(
              width: 22,
              height: 22,
              child: Checkbox(
                value: checked,
                onChanged: (v) => onChanged(v ?? false),
              ),
            ),
            const SizedBox(width: 8),
            Expanded(
              child: Text(
                option.label(s.isAr),
                style: const TextStyle(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.w600,
                  fontSize: 13,
                  height: 1.35,
                ),
              ),
            ),
            if (showAddProduct) ...[
              const SizedBox(width: 8),
              Container(
                padding:
                    const EdgeInsets.symmetric(horizontal: 8, vertical: 5),
                decoration: BoxDecoration(
                  color: AppTheme.background,
                  borderRadius: BorderRadius.circular(6),
                  border: Border.all(color: AppTheme.border),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.add, size: 13, color: AppTheme.textPrimary),
                    const SizedBox(width: 3),
                    Text(
                      s.t('addProduct'),
                      style: const TextStyle(
                        color: AppTheme.textPrimary,
                        fontSize: 11,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

/// Read-only field that opens a picker sheet on tap (used for "Violator").
class _PickerField extends StatelessWidget {
  final String label;
  final String? value;
  final String hint;
  final VoidCallback onTap;

  const _PickerField({
    required this.label,
    required this.value,
    required this.hint,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final hasValue = value != null && value!.isNotEmpty;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.w600,
            fontSize: 14,
          ),
        ),
        const SizedBox(height: 8),
        GestureDetector(
          onTap: onTap,
          behavior: HitTestBehavior.opaque,
          child: Container(
            height: AppTheme.inputHeight,
            padding: const EdgeInsets.symmetric(horizontal: 14),
            decoration: BoxDecoration(
              color: Colors.white,
              border: Border.all(color: AppTheme.border),
              borderRadius: BorderRadius.circular(AppTheme.inputRadius),
            ),
            child: Row(
              children: [
                Expanded(
                  child: Text(
                    hasValue ? value! : hint,
                    style: TextStyle(
                      color: hasValue
                          ? AppTheme.textPrimary
                          : AppTheme.textSecondary,
                      fontSize: 14,
                    ),
                  ),
                ),
                const Icon(Icons.keyboard_arrow_down_rounded,
                    color: AppTheme.textPrimary),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

/// Radio-style selectable row.
class _RadioRow extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;
  const _RadioRow({
    required this.label,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 6),
        child: Row(
          children: [
            Icon(
              selected ? Icons.radio_button_checked : Icons.radio_button_off,
              color: selected ? AppTheme.primary : AppTheme.textSecondary,
              size: 22,
            ),
            const SizedBox(width: 8),
            Flexible(
              child: Text(
                label,
                style: TextStyle(
                  color: AppTheme.textPrimary,
                  fontWeight: selected ? FontWeight.w700 : FontWeight.w500,
                  fontSize: 14,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
