import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import '../core/localization/app_strings.dart';
import '../data/models/non_compliance_models.dart';
import '../theme/app_theme.dart';
import 'app_bottom_sheet.dart';
import 'common_buttons.dart';
import 'custom_text_field.dart';
import 'image_upload_box.dart';
import 'specify_violations_sheet.dart';
import 'success_banner.dart';
import 'violation_details_sheet.dart';

/// Opens the "Non-compliance reasons" bottom sheet for a clause. Returns the
/// completed [NonComplianceEntry] on Add, or null if dismissed.
/// Reference: Sprint2 screenshots 1 & 5.
Future<NonComplianceEntry?> showNonComplianceSheet(
  BuildContext context, {
  required String clause,
}) {
  return showModalBottomSheet<NonComplianceEntry>(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (_) => _NonComplianceSheet(clause: clause),
  );
}

class _NonComplianceSheet extends StatefulWidget {
  final String clause;
  const _NonComplianceSheet({required this.clause});

  @override
  State<_NonComplianceSheet> createState() => _NonComplianceSheetState();
}

class _NonComplianceSheetState extends State<_NonComplianceSheet> {
  late final NonComplianceEntry _entry;
  final _otherReason = TextEditingController();
  final _notes = TextEditingController();
  bool _showSavedBanner = false;
  bool _picking = false;

  @override
  void initState() {
    super.initState();
    _entry = NonComplianceEntry(clause: widget.clause);
  }

  @override
  void dispose() {
    _otherReason.dispose();
    _notes.dispose();
    super.dispose();
  }

  Future<void> _addViolation() async {
    final v = await showSpecifyViolationsSheet(context);
    if (v != null && mounted) {
      setState(() {
        _entry.violations.add(v);
        _showSavedBanner = true;
      });
    }
  }

  Future<void> _editViolation(Violation v) async {
    final updated = await showViolationDetailsSheet(context, violation: v);
    if (updated != null && mounted) setState(() {});
  }

  Future<void> _pickImage() async {
    setState(() => _picking = true);
    try {
      final picked = await ImagePicker().pickImage(source: ImageSource.gallery);
      if (picked != null && mounted) {
        setState(() => _entry.attachmentCount++);
      }
    } catch (_) {
      // picker unavailable / cancelled
    } finally {
      if (mounted) setState(() => _picking = false);
    }
  }

  void _onAdd() {
    // _entry.reason is already kept in sync by the dropdown's onChanged.
    _entry.otherReason = _otherReason.text.trim();
    _entry.inspectorNotes = _notes.text.trim();
    Navigator.of(context).pop(_entry);
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return AppBottomSheet(
      title: s.t('nonComplianceReasons'),
      footer: PrimaryButton(label: s.t('add'), onPressed: _onAdd),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (_showSavedBanner) ...[
            SuccessBanner(
              message: s.t('dataSavedSuccessfully'),
              onClose: () => setState(() => _showSavedBanner = false),
            ),
            const SizedBox(height: 16),
          ],

          // Clause (read-only)
          _FieldLabel(s.t('clause')),
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              color: Colors.white,
              border: Border.all(color: AppTheme.border),
              borderRadius: BorderRadius.circular(AppTheme.inputRadius),
            ),
            child: Text(
              widget.clause,
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontSize: 14,
                height: 1.4,
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Reason dropdown
          CustomDropdownField<CatalogOption>(
            label: s.t('reasonForNonCompliance'),
            value: _entry.reason,
            hint: s.t('reasonForNonCompliance'),
            items: MockViolationCatalog.reasons
                .map((o) => DropdownMenuItem(value: o, child: Text(o.label(s.isAr))))
                .toList(),
            onChanged: (o) => setState(() => _entry.reason = o),
          ),
          const SizedBox(height: 16),

          // Other reason
          CustomTextField(
            label: s.t('otherReason'),
            hint: s.t('enterReasonShort'),
            controller: _otherReason,
            maxLines: 3,
          ),
          const SizedBox(height: 18),

          // Violations
          _FieldLabel(s.t('selectViolationsAndDetails')),
          SecondaryButton(
            label: s.t('addViolation'),
            icon: Icons.add,
            onPressed: _addViolation,
          ),
          for (var i = 0; i < _entry.violations.length; i++) ...[
            const SizedBox(height: 12),
            _ViolationCard(
              index: i + 1,
              violation: _entry.violations[i],
              onEdit: () => _editViolation(_entry.violations[i]),
            ),
          ],
          const SizedBox(height: 18),

          // Inspector notes
          CustomTextField(
            label: s.t('inspectorNotes'),
            hint: s.t('notes'),
            controller: _notes,
            maxLines: 3,
          ),
          const SizedBox(height: 18),

          // Images & attachments
          _FieldLabel(s.t('imagesAndAttachments')),
          ImageUploadBox(onTap: _pickImage, busy: _picking),
          if (_entry.attachmentCount > 0) ...[
            const SizedBox(height: 8),
            Text(
              '${_entry.attachmentCount} ${s.t('imagesAndAttachments')}',
              style: const TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 12,
              ),
            ),
          ],
        ],
      ),
    );
  }
}

class _FieldLabel extends StatelessWidget {
  final String text;
  const _FieldLabel(this.text);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Text(
        text,
        style: const TextStyle(
          color: AppTheme.textPrimary,
          fontWeight: FontWeight.w600,
          fontSize: 14,
        ),
      ),
    );
  }
}

/// Summary card for an added violation (Sprint2 screenshot 5).
class _ViolationCard extends StatelessWidget {
  final int index;
  final Violation violation;
  final VoidCallback onEdit;

  const _ViolationCard({
    required this.index,
    required this.violation,
    required this.onEdit,
  });

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final penaltyLabels = MockViolationCatalog.subsequentPenalties
        .where((p) => violation.penalties.contains(p.id))
        .map((p) => p.label(s.isAr))
        .join(s.isAr ? '، ' : ', ');

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border.all(color: AppTheme.border),
        borderRadius: BorderRadius.circular(AppTheme.cardRadius),
      ),
      padding: const EdgeInsets.all(14),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '${s.t('addViolation')} $index',
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w700,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            violation.code,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontSize: 13,
              height: 1.4,
            ),
          ),
          const SizedBox(height: 12),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: _CardField(
                  label: s.t('numberOfUnits'),
                  value: '${violation.unitCount}',
                ),
              ),
              Expanded(
                child: _CardField(
                  label: s.t('offender'),
                  value: violation.violator?.label(s.isAr) ?? '—',
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          _CardField(
            label: s.t('penalty'),
            value: penaltyLabels.isEmpty ? '—' : penaltyLabels,
          ),
          const SizedBox(height: 12),
          GestureDetector(
            onTap: onEdit,
            behavior: HitTestBehavior.opaque,
            child: Container(
              height: 44,
              padding: const EdgeInsetsDirectional.only(start: 14, end: 6),
              decoration: BoxDecoration(
                color: Colors.white,
                border: Border.all(color: AppTheme.border),
                borderRadius: BorderRadius.circular(AppTheme.buttonRadius),
              ),
              child: Row(
                children: [
                  Expanded(
                    child: Text(
                      s.t('editViolation'),
                      style: const TextStyle(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w600,
                        fontSize: 13,
                      ),
                    ),
                  ),
                  Container(
                    width: 32,
                    height: 32,
                    decoration: BoxDecoration(
                      color: AppTheme.primary,
                      borderRadius: BorderRadius.circular(7),
                    ),
                    child: const Icon(Icons.arrow_forward,
                        color: Colors.white, size: 16),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _CardField extends StatelessWidget {
  final String label;
  final String value;
  const _CardField({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(color: AppTheme.textSecondary, fontSize: 11),
        ),
        const SizedBox(height: 3),
        Text(
          value,
          style: const TextStyle(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.w600,
            fontSize: 13,
            height: 1.35,
          ),
        ),
      ],
    );
  }
}
