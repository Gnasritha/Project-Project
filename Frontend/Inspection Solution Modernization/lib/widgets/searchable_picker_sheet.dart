import 'package:flutter/material.dart';
import '../core/localization/app_strings.dart';
import '../data/models/non_compliance_models.dart';
import '../theme/app_theme.dart';
import 'app_bottom_sheet.dart';

/// Opens a searchable single-select bottom sheet (search field + radio list).
/// Used for the "Violator" field on the Violation details sheet — reference:
/// Sprint2 screenshots 3 & 4. Returns the chosen option, or null if dismissed.
Future<CatalogOption?> showSearchablePicker({
  required BuildContext context,
  required String title,
  required List<CatalogOption> options,
  CatalogOption? selected,
}) {
  return showModalBottomSheet<CatalogOption>(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (_) => _SearchablePickerSheet(
      title: title,
      options: options,
      selected: selected,
    ),
  );
}

class _SearchablePickerSheet extends StatefulWidget {
  final String title;
  final List<CatalogOption> options;
  final CatalogOption? selected;

  const _SearchablePickerSheet({
    required this.title,
    required this.options,
    this.selected,
  });

  @override
  State<_SearchablePickerSheet> createState() => _SearchablePickerSheetState();
}

class _SearchablePickerSheetState extends State<_SearchablePickerSheet> {
  final _search = TextEditingController();
  String _query = '';

  @override
  void dispose() {
    _search.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final filtered = _query.isEmpty
        ? widget.options
        : widget.options
            .where((o) =>
                o.label(s.isAr).toLowerCase().contains(_query.toLowerCase()))
            .toList();

    return AppBottomSheet(
      title: widget.title,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Search field
          TextField(
            controller: _search,
            onChanged: (v) => setState(() => _query = v),
            style: const TextStyle(fontSize: 14, color: AppTheme.textPrimary),
            decoration: InputDecoration(
              hintText: s.t('searchHere'),
              prefixIcon: const Icon(Icons.search,
                  color: AppTheme.textSecondary, size: 20),
              filled: true,
              fillColor: Colors.white,
              contentPadding:
                  const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
              enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(AppTheme.inputRadius),
                borderSide: const BorderSide(color: AppTheme.border),
              ),
              focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(AppTheme.inputRadius),
                borderSide: const BorderSide(color: AppTheme.primary, width: 1.4),
              ),
            ),
          ),
          const SizedBox(height: 14),

          if (filtered.isEmpty)
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 24),
              child: Center(
                child: Text(
                  s.t('noDataFound'),
                  style: const TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 13,
                  ),
                ),
              ),
            )
          else
            for (final o in filtered)
              Padding(
                padding: const EdgeInsets.only(bottom: 10),
                child: _OptionRow(
                  option: o,
                  selected: o == widget.selected,
                  onTap: () => Navigator.of(context).pop(o),
                ),
              ),
        ],
      ),
    );
  }
}

class _OptionRow extends StatelessWidget {
  final CatalogOption option;
  final bool selected;
  final VoidCallback onTap;

  const _OptionRow({
    required this.option,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final isAr = AppStrings.of(context).isAr;
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
        decoration: BoxDecoration(
          color: Colors.white,
          border: Border.all(
            color: selected ? AppTheme.primary : AppTheme.border,
          ),
          borderRadius: BorderRadius.circular(AppTheme.inputRadius),
        ),
        child: Row(
          children: [
            Icon(
              selected ? Icons.radio_button_checked : Icons.radio_button_off,
              color: selected ? AppTheme.primary : AppTheme.textSecondary,
              size: 22,
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Text(
                option.label(isAr),
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
