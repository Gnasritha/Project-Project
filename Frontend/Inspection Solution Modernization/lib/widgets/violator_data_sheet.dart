import 'package:flutter/material.dart';
import '../core/api/api_exception.dart';
import '../core/localization/app_strings.dart';
import '../data/models/violator.dart';
import '../services/violator_service.dart';
import '../theme/app_theme.dart';
import 'app_bottom_sheet.dart';
import 'common_buttons.dart';
import 'custom_text_field.dart';

/// Opens the "Fill in [name] data" bottom sheet. Returns the edited [Violator]
/// when the inspector taps Add, or null if dismissed.
/// Reference: Sprint2 screenshots 8-11.
Future<Violator?> showViolatorDataSheet(
  BuildContext context, {
  required Violator violator,
}) {
  return showModalBottomSheet<Violator>(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (_) => _ViolatorDataSheet(violator: violator),
  );
}

class _ViolatorDataSheet extends StatefulWidget {
  final Violator violator;
  const _ViolatorDataSheet({required this.violator});

  @override
  State<_ViolatorDataSheet> createState() => _ViolatorDataSheetState();
}

class _ViolatorDataSheetState extends State<_ViolatorDataSheet> {
  late ViolatorCategory _category;
  late VerificationState _verification;
  bool _verifying = false;

  final _facilityNumber = TextEditingController();
  final _idNumber = TextEditingController();
  final _birthDate = TextEditingController();

  @override
  void initState() {
    super.initState();
    final v = widget.violator;
    _category = v.category;
    _verification = v.verification;
    _facilityNumber.text = v.nationalFacilityNumber ?? '';
    _idNumber.text = v.idNumber ?? '';
    _birthDate.text = v.birthDate ?? '';
  }

  @override
  void dispose() {
    _facilityNumber.dispose();
    _idNumber.dispose();
    _birthDate.dispose();
    super.dispose();
  }

  /// Hits the backend verification endpoint. Sets the violator's name when
  /// the registry returns it so the accordion card on the Violators Info
  /// screen reads the live value instead of the placeholder.
  Future<void> _verify() async {
    setState(() => _verifying = true);
    try {
      ViolatorVerifyResult result;
      if (_category == ViolatorCategory.entity) {
        result = await ViolatorService.instance.verifyEntity(
          nationalFacilityNumber: _facilityNumber.text.trim(),
        );
      } else {
        result = await ViolatorService.instance.verifyIndividual(
          idNumber: _idNumber.text.trim(),
          birthDate: _birthDate.text.trim(),
        );
      }
      if (!mounted) return;
      setState(() {
        _verifying = false;
        _verification = result.verified
            ? VerificationState.verified
            : VerificationState.failed;
        if (result.verified && (result.name ?? '').isNotEmpty) {
          widget.violator.name = result.name!;
        }
      });
    } on ApiException {
      if (!mounted) return;
      setState(() {
        _verifying = false;
        _verification = VerificationState.failed;
      });
    }
  }

  Future<void> _pickBirthDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: DateTime(1990),
      firstDate: DateTime(1920),
      lastDate: now,
    );
    if (picked != null) {
      setState(() {
        _birthDate.text = '${picked.year}/${picked.month}/${picked.day}';
        _verification = VerificationState.idle;
      });
    }
  }

  void _onAdd() {
    final v = widget.violator;
    v.category = _category;
    v.verification = _verification;
    v.dataFilled = true;
    // A successful verify means the violator is on record — preselect
    // "Yes, identified" so it shows green on the Violators Info screen
    // automatically, without the inspector having to tap the chip.
    if (_verification == VerificationState.verified) {
      v.identified = true;
    }
    if (_category == ViolatorCategory.entity) {
      v.nationalFacilityNumber = _facilityNumber.text.trim();
      v.idNumber = null;
      v.birthDate = null;
    } else {
      v.idNumber = _idNumber.text.trim();
      v.birthDate = _birthDate.text.trim();
      v.nationalFacilityNumber = null;
    }
    Navigator.of(context).pop(v);
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return AppBottomSheet(
      title: '${s.t('fillData')} — ${widget.violator.name}',
      footer: PrimaryButton(label: s.t('add'), onPressed: _onAdd),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (_verification == VerificationState.failed) ...[
            _ErrorBanner(message: s.t('invalidNationalId')),
            const SizedBox(height: 16),
          ],

          // Violator category
          Text(
            s.t('violatorCategory'),
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
                child: _CategoryRadio(
                  label: s.t('entity'),
                  selected: _category == ViolatorCategory.entity,
                  onTap: () => setState(() {
                    _category = ViolatorCategory.entity;
                    _verification = VerificationState.idle;
                  }),
                ),
              ),
              Expanded(
                child: _CategoryRadio(
                  label: s.t('individual'),
                  selected: _category == ViolatorCategory.individual,
                  onTap: () => setState(() {
                    _category = ViolatorCategory.individual;
                    _verification = VerificationState.idle;
                  }),
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),

          if (_category == ViolatorCategory.entity)
            ..._entityFields(s)
          else
            ..._individualFields(s),
        ],
      ),
    );
  }

  List<Widget> _entityFields(AppStrings s) {
    return [
      Text(
        s.t('nationalFacilityNumber'),
        style: const TextStyle(
          color: AppTheme.textPrimary,
          fontWeight: FontWeight.w600,
          fontSize: 14,
        ),
      ),
      const SizedBox(height: 8),
      if (_verification == VerificationState.verified)
        _VerifiedBanner(label: s.t('verified'))
      else
        _InlineVerifyField(
          controller: _facilityNumber,
          hint: s.t('nationalFacilityNumber'),
          editable: true,
          failed: _verification == VerificationState.failed,
          failedText: s.t('verificationFailed'),
          busy: _verifying,
          verifyLabel: s.t('verify'),
          onVerify: _verify,
          keyboardType: TextInputType.number,
        ),
    ];
  }

  List<Widget> _individualFields(AppStrings s) {
    return [
      CustomTextField(
        label: s.t('idNumber'),
        hint: s.t('idNumber'),
        controller: _idNumber,
        keyboardType: TextInputType.number,
        onChanged: (_) {
          if (_verification != VerificationState.idle) {
            setState(() => _verification = VerificationState.idle);
          }
        },
      ),
      const SizedBox(height: 16),
      CustomTextField(
        label: s.t('birthDate'),
        hint: 'YYYY/MM/DD',
        controller: _birthDate,
        readOnly: true,
        onTap: _pickBirthDate,
        suffixIcon: IconButton(
          onPressed: _pickBirthDate,
          icon: const Icon(Icons.calendar_today_outlined,
              size: 18, color: AppTheme.textSecondary),
        ),
      ),
      const SizedBox(height: 16),
      if (_verification == VerificationState.verified)
        _VerifiedBanner(label: s.t('verified'))
      else
        _InlineVerifyField(
          hint: s.t('checkEnteredData'),
          editable: false,
          failed: _verification == VerificationState.failed,
          failedText: s.t('verificationFailed'),
          busy: _verifying,
          verifyLabel: s.t('verify'),
          onVerify: _verify,
        ),
    ];
  }
}

/// Radio-style category selector row.
class _CategoryRadio extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;
  const _CategoryRadio({
    required this.label,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
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
    );
  }
}

/// Bordered field with a trailing green Verify button. When [editable] is
/// false it only renders status text (used for the individual "check the
/// entered data" row).
class _InlineVerifyField extends StatelessWidget {
  final TextEditingController? controller;
  final String hint;
  final bool editable;
  final bool failed;
  final String failedText;
  final bool busy;
  final String verifyLabel;
  final VoidCallback onVerify;
  final TextInputType? keyboardType;

  const _InlineVerifyField({
    this.controller,
    required this.hint,
    required this.editable,
    required this.failed,
    required this.failedText,
    required this.busy,
    required this.verifyLabel,
    required this.onVerify,
    this.keyboardType,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border.all(color: failed ? AppTheme.error : AppTheme.border),
        borderRadius: BorderRadius.circular(AppTheme.inputRadius),
      ),
      padding: const EdgeInsetsDirectional.only(start: 14, end: 6),
      child: Row(
        children: [
          Expanded(
            child: editable
                ? TextField(
                    controller: controller,
                    keyboardType: keyboardType,
                    style: const TextStyle(
                        fontSize: 14, color: AppTheme.textPrimary),
                    decoration: InputDecoration(
                      hintText: hint,
                      border: InputBorder.none,
                      hintStyle: const TextStyle(
                          color: AppTheme.textSecondary, fontSize: 14),
                    ),
                  )
                : Padding(
                    padding: const EdgeInsets.symmetric(vertical: 14),
                    child: Text(
                      failed ? failedText : hint,
                      style: TextStyle(
                        color: failed ? AppTheme.error : AppTheme.textSecondary,
                        fontSize: 14,
                        fontWeight:
                            failed ? FontWeight.w600 : FontWeight.w400,
                      ),
                    ),
                  ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 6),
            child: ElevatedButton(
              onPressed: busy ? null : onVerify,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppTheme.primary,
                minimumSize: const Size(72, 36),
                padding: const EdgeInsets.symmetric(horizontal: 14),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8)),
              ),
              child: busy
                  ? const SizedBox(
                      width: 16,
                      height: 16,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        valueColor: AlwaysStoppedAnimation(Colors.white),
                      ),
                    )
                  : Text(
                      verifyLabel,
                      style: const TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.w700,
                        fontSize: 13,
                      ),
                    ),
            ),
          ),
        ],
      ),
    );
  }
}

/// Green "Verified" confirmation banner.
class _VerifiedBanner extends StatelessWidget {
  final String label;
  const _VerifiedBanner({required this.label});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
      decoration: BoxDecoration(
        color: const Color(0xFFE9F7EF),
        border: Border.all(color: AppTheme.success.withValues(alpha: 0.40)),
        borderRadius: BorderRadius.circular(AppTheme.inputRadius),
      ),
      child: Row(
        children: [
          Expanded(
            child: Text(
              label,
              style: const TextStyle(
                color: AppTheme.success,
                fontWeight: FontWeight.w700,
                fontSize: 14,
              ),
            ),
          ),
          Container(
            width: 24,
            height: 24,
            decoration: const BoxDecoration(
              color: AppTheme.success,
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.check, color: Colors.white, size: 15),
          ),
        ],
      ),
    );
  }
}

/// Red invalid-information banner shown above the form on a failed verify.
class _ErrorBanner extends StatelessWidget {
  final String message;
  const _ErrorBanner({required this.message});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
      decoration: BoxDecoration(
        color: const Color(0xFFFCEBEC),
        border: Border.all(color: AppTheme.error.withValues(alpha: 0.45)),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Icon(Icons.error_outline, color: AppTheme.error, size: 18),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              message,
              style: const TextStyle(
                color: AppTheme.error,
                fontSize: 12.5,
                fontWeight: FontWeight.w500,
                height: 1.4,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
