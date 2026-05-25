import 'package:flutter/material.dart';
import '../core/localization/app_strings.dart';
import '../data/models/non_compliance_models.dart';
import '../theme/app_theme.dart';
import 'app_bottom_sheet.dart';
import 'common_buttons.dart';
import 'violation_details_sheet.dart';

/// Opens the "Specify violations" bottom sheet. The inspector ticks the
/// violations that apply, then "Enter Violation Details" chains into the
/// details sheet. Returns the completed [Violation], or null if dismissed.
/// Reference: Sprint2 screenshot 2.
Future<Violation?> showSpecifyViolationsSheet(BuildContext context) {
  return showModalBottomSheet<Violation>(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (_) => const _SpecifyViolationsSheet(),
  );
}

class _SpecifyViolationsSheet extends StatefulWidget {
  const _SpecifyViolationsSheet();

  @override
  State<_SpecifyViolationsSheet> createState() =>
      _SpecifyViolationsSheetState();
}

class _SpecifyViolationsSheetState extends State<_SpecifyViolationsSheet> {
  final Set<String> _selected = {};

  Future<void> _enterDetails() async {
    final option = MockViolationCatalog.violationOptions
        .firstWhere((o) => o.id == _selected.first);
    final violation = Violation(
      code: MockViolationCatalog.codeForOption(
          option.id, AppStrings.of(context).isAr),
    );
    final result = await showViolationDetailsSheet(context, violation: violation);
    if (result != null && mounted) {
      Navigator.of(context).pop(result);
    }
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return AppBottomSheet(
      title: s.t('specifyViolations'),
      footer: PrimaryButton(
        label: s.t('enterViolationDetails'),
        onPressed: _selected.isEmpty ? null : _enterDetails,
      ),
      child: Column(
        children: [
          for (final o in MockViolationCatalog.violationOptions)
            Padding(
              padding: const EdgeInsets.only(bottom: 10),
              child: GestureDetector(
                onTap: () => setState(() {
                  _selected.contains(o.id)
                      ? _selected.remove(o.id)
                      : _selected.add(o.id);
                }),
                behavior: HitTestBehavior.opaque,
                child: Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    border: Border.all(
                      color: _selected.contains(o.id)
                          ? AppTheme.primary
                          : AppTheme.border,
                    ),
                    borderRadius: BorderRadius.circular(AppTheme.inputRadius),
                  ),
                  child: Row(
                    children: [
                      SizedBox(
                        width: 22,
                        height: 22,
                        child: Checkbox(
                          value: _selected.contains(o.id),
                          onChanged: (v) => setState(() {
                            (v ?? false)
                                ? _selected.add(o.id)
                                : _selected.remove(o.id);
                          }),
                        ),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: Text(
                          o.label(s.isAr),
                          style: const TextStyle(
                            color: AppTheme.textPrimary,
                            fontWeight: FontWeight.w600,
                            fontSize: 14,
                            height: 1.35,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
