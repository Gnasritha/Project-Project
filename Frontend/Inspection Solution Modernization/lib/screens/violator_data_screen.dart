import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/api/api_exception.dart';
import '../core/localization/app_strings.dart';
import '../data/models/visit_dtos.dart';
import '../routes/app_routes.dart';
import '../services/session_state.dart';
import '../services/visit_service.dart';
import '../theme/app_theme.dart';
import '../utils/extensions.dart';
import '../widgets/common_buttons.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/screen_scaffold.dart';
import 'map_location_sheet.dart';

enum _LicenseAccess { number, map }

class ViolatorDataScreen extends StatefulWidget {
  const ViolatorDataScreen({super.key});

  @override
  State<ViolatorDataScreen> createState() => _ViolatorDataScreenState();
}

class _ViolatorDataScreenState extends State<ViolatorDataScreen> {
  bool _hasLicense = true;
  String _licenseType = 'Commercial License';
  _LicenseAccess _access = _LicenseAccess.number;
  final _licenseController = TextEditingController();

  bool _verifying = false;
  bool _creatingVisit = false;
  String? _error;
  LicenseVerificationResponse? _verified;

  /// Location picked from the map on the no-license path ("lat, lng").
  String? _pickedLocation;

  @override
  void dispose() {
    _licenseController.dispose();
    super.dispose();
  }

  Future<void> _verify() async {
    final license = _licenseController.text.trim();
    if (license.isEmpty) {
      setState(() => _error = 'Enter a license number');
      return;
    }
    setState(() {
      _verifying = true;
      _error = null;
    });
    try {
      final info = await VisitService.instance.verifyLicense(licenseNumber: license);
      if (!mounted) return;
      // The backend answers HTTP 200 even for an unknown licence — treat
      // found == false as a validation error, not a successful verify.
      if (!info.found) {
        setState(() {
          _verifying = false;
          _verified = null;
          _error = (info.message != null && info.message!.isNotEmpty)
              ? info.message
              : AppStrings.of(context).t('noDataFound');
        });
        return;
      }
      context.read<SessionState>().setLicense(licenseNumber: license, info: info);
      setState(() {
        _verifying = false;
        _verified = info;
        _error = null;
      });
    } on ApiException catch (e) {
      if (!mounted) return;
      setState(() {
        _verifying = false;
        _verified = null;
        _error = e.statusCode == 404
            ? AppStrings.of(context).t('noDataFound')
            : e.message;
      });
    }
  }

  /// "Select from the map" — open the map bottom sheet; the confirmed
  /// location is stored so the Facility Status screen pre-fills its
  /// Location field.
  Future<void> _openLocationSheet() async {
    final result = await showModalBottomSheet<String>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => const MapLocationSheet(),
    );
    if (result != null && mounted) {
      setState(() => _pickedLocation = result);
      context.read<SessionState>().setPickedLocation(result);
    }
  }

  Future<void> _onNext() async {
    final session = context.read<SessionState>();

    final type = session.selectedType;
    if (type == null) {
      context.showSnack('Select a control type', color: AppTheme.warning);
      Navigator.of(context).pushReplacementNamed(Routes.visits);
      return;
    }

    // ── Map-based path — either no licence, or "Select from the map" was
    //    chosen for the licence access. The facility location comes from the
    //    map; there is no licence number to verify here.
    if (!_hasLicense || _access == _LicenseAccess.map) {
      if (_pickedLocation == null) {
        context.showSnack('Select the location from the map first',
            color: AppTheme.warning);
        return;
      }
      Navigator.of(context).pushNamed(Routes.facilityStatus);
      return;
    }

    // ── License-number path — the licence must be verified first.
    if (_verified == null) {
      context.showSnack('Verify the license first', color: AppTheme.warning);
      return;
    }

    setState(() => _creatingVisit = true);
    try {
      final number = 'INS-${DateTime.now().millisecondsSinceEpoch}';
      final req = VisitCreateRequest(
        inspectionNumber: number,
        inspectionTypeId: type.id,
        establishmentId: _verified?.establishmentId,
        licenseId: _verified?.licenseId,
        licenseAvailable: true,
        licenseEntryMethod: _access == _LicenseAccess.map ? 'MAP' : 'LICENSE_NUMBER',
        inspectionDate: DateTime.now(),
        latitude: _verified?.latitude,
        longitude: _verified?.longitude,
      );

      final res = await VisitService.instance
          .createVisit(req, inspectorId: session.inspectorId);

      if (!mounted) return;
      // Carry the backend business key so it shows as the case ID in the UI.
      session.setVisit(
        inspectionId: res.inspectionId,
        inspectionNumber: res.inspectionNumber ?? number,
        businessKey: res.businessKey,
        processInstanceKey: res.processInstanceKey,
      );
      Navigator.of(context).pushNamed(Routes.facilityStatus);
    } on ApiException catch (e) {
      if (!mounted) return;
      context.showSnack(e.message, color: AppTheme.error);
    } finally {
      if (mounted) setState(() => _creatingVisit = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);

    return InspectionScreenScaffold(
      title: s.t('violatorData'),
      totalSteps: 6,
      currentStep: 1,
      footer: FooterNavBar(
        nextLabel: s.t('next'),
        onNext: _onNext,
        nextLoading: _creatingVisit,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            s.t('isThereLicense'),
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w700,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 10),
          Row(
            children: [
              Expanded(
                child: StatusChip(
                  label: s.t('yesLicense'),
                  active: _hasLicense,
                  activeColor: AppTheme.secondary,
                  onTap: () => setState(() => _hasLicense = true),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: StatusChip(
                  label: s.t('noLicense'),
                  active: !_hasLicense,
                  activeColor: AppTheme.secondary,
                  onTap: () => setState(() {
                    _hasLicense = false;
                    _verified = null;
                    _error = null;
                  }),
                ),
              ),
            ],
          ),
          if (_hasLicense) ...[
            const SizedBox(height: 22),
            Text(
              s.t('addLicenseData'),
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w700,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 14),
            CustomDropdownField<String>(
              label: s.t('defineBasedOn'),
              value: _licenseType,
              items: const [
                DropdownMenuItem(
                    value: 'Commercial License', child: Text('Commercial License')),
                DropdownMenuItem(
                    value: 'Professional License', child: Text('Professional License')),
              ],
              onChanged: (v) => setState(() => _licenseType = v ?? 'Commercial License'),
            ),
            const SizedBox(height: 18),
            Text(
              s.t('access'),
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w700,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: _RadioOption(
                    label: s.t('licenseNumber'),
                    selected: _access == _LicenseAccess.number,
                    onTap: () => setState(() => _access = _LicenseAccess.number),
                  ),
                ),
                Expanded(
                  child: _RadioOption(
                    label: s.t('selectFromMap'),
                    selected: _access == _LicenseAccess.map,
                    // Selecting this opens the map bottom sheet directly.
                    onTap: () {
                      setState(() => _access = _LicenseAccess.map);
                      _openLocationSheet();
                    },
                  ),
                ),
              ],
            ),
            if (_error != null) ...[
              const SizedBox(height: 14),
              _ErrorBanner(message: _error!),
            ],
            const SizedBox(height: 14),
            Text(
              s.t('licenseNumber'),
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w600,
                fontSize: 13,
              ),
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: CustomTextField(
                    hint: s.t('licenseNumber'),
                    controller: _licenseController,
                    keyboardType: TextInputType.text,
                    errorState: _error != null,
                    suffixIcon: _verified != null
                        ? Padding(
                            padding: const EdgeInsetsDirectional.only(end: 8),
                            child: Container(
                              decoration: const BoxDecoration(
                                color: AppTheme.success,
                                shape: BoxShape.circle,
                              ),
                              padding: const EdgeInsets.all(4),
                              child: const Icon(Icons.check,
                                  color: Colors.white, size: 14),
                            ),
                          )
                        : Padding(
                            padding: const EdgeInsetsDirectional.only(end: 6),
                            child: ElevatedButton(
                              onPressed: _verifying ? null : _verify,
                              style: ElevatedButton.styleFrom(
                                backgroundColor: AppTheme.primary,
                                minimumSize: const Size(70, 36),
                                padding:
                                    const EdgeInsets.symmetric(horizontal: 14),
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                elevation: 0,
                              ),
                              child: _verifying
                                  ? const SizedBox(
                                      width: 16,
                                      height: 16,
                                      child: CircularProgressIndicator(
                                        strokeWidth: 1.6,
                                        color: Colors.white,
                                      ),
                                    )
                                  : Text(
                                      s.t('verify'),
                                      style: const TextStyle(
                                        color: Colors.white,
                                        fontSize: 13,
                                        fontWeight: FontWeight.w600,
                                      ),
                                    ),
                            ),
                          ),
                  ),
                ),
                const SizedBox(width: 8),
                Container(
                  width: 52,
                  height: 52,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    border: Border.all(color: AppTheme.border),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: const Icon(Icons.qr_code_scanner_rounded,
                      color: AppTheme.textPrimary),
                ),
              ],
            ),
            if (_verified != null) ...[
              const SizedBox(height: 18),
              _LicenseDetailsCard(info: _verified!),
            ],
            const SizedBox(height: 18),
            const _DisclosureCard(),
          ] else ...[
            // No-license path: pick the establishment location from the map
            // bottom sheet; the confirmed location flows to Facility Status.
            const SizedBox(height: 22),
            SecondaryButton(
              label: s.t('selectFromMap'),
              icon: Icons.map_outlined,
              onPressed: _openLocationSheet,
            ),
          ],
        ],
      ),
    );
  }
}

class _RadioOption extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;
  const _RadioOption({required this.label, required this.selected, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Row(
          children: [
            Container(
              width: 18,
              height: 18,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                border: Border.all(
                  color: selected ? AppTheme.primary : AppTheme.textSecondary,
                  width: 2,
                ),
              ),
              child: selected
                  ? Center(
                      child: Container(
                        width: 8,
                        height: 8,
                        decoration: const BoxDecoration(
                          color: AppTheme.primary,
                          shape: BoxShape.circle,
                        ),
                      ),
                    )
                  : null,
            ),
            const SizedBox(width: 8),
            Flexible(
              child: Text(
                label,
                style: TextStyle(
                  color: selected ? AppTheme.textPrimary : AppTheme.textSecondary,
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

class _ErrorBanner extends StatelessWidget {
  final String message;
  const _ErrorBanner({required this.message});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
      decoration: BoxDecoration(
        color: const Color(0xFFFDECEA),
        border: Border.all(color: AppTheme.error.withValues(alpha: 0.3)),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        children: [
          const Icon(Icons.error_outline, color: AppTheme.error, size: 18),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              message,
              style: const TextStyle(
                color: AppTheme.error,
                fontSize: 13,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _LicenseDetailsCard extends StatelessWidget {
  final LicenseVerificationResponse info;
  const _LicenseDetailsCard({required this.info});

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final headerName = info.establishmentNameAr ?? info.establishmentName ?? '';
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppTheme.cardRadius),
        boxShadow: AppTheme.cardShadow,
      ),
      clipBehavior: Clip.antiAlias,
      child: Column(
        children: [
          Container(
            color: AppTheme.primary,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
            child: Row(
              children: [
                Expanded(
                  child: Text(
                    headerName,
                    style: const TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.w700,
                      fontSize: 16,
                    ),
                  ),
                ),
                const Icon(Icons.more_vert, color: Colors.white),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _Pair(
                  label: s.t('licenseNumber'),
                  value: info.licenseNumber,
                  label2: s.t('licenseStatus'),
                  value2: info.licenseStatus ?? '—',
                ),
                const SizedBox(height: 14),
                _Pair(
                  label: s.t('licenseType'),
                  value: info.licenseType ?? '—',
                  label2: s.t('mobileNumber'),
                  value2: info.mobileNumber ?? '—',
                ),
                const SizedBox(height: 14),
                Container(
                  decoration: BoxDecoration(
                    border: Border.all(color: AppTheme.border),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  padding: const EdgeInsets.fromLTRB(14, 8, 8, 8),
                  child: Row(
                    children: [
                      Expanded(
                        child: Text(
                          s.t('moreDetails'),
                          style: const TextStyle(
                            color: AppTheme.textPrimary,
                            fontWeight: FontWeight.w600,
                            fontSize: 14,
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
            ),
          ),
        ],
      ),
    );
  }
}

class _Pair extends StatelessWidget {
  final String label, value, label2, value2;
  const _Pair({
    required this.label,
    required this.value,
    required this.label2,
    required this.value2,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label,
                  style: const TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  )),
              const SizedBox(height: 4),
              Text(value,
                  style: const TextStyle(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                    fontSize: 14,
                  )),
            ],
          ),
        ),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label2,
                  style: const TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  )),
              const SizedBox(height: 4),
              Text(value2,
                  style: const TextStyle(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                    fontSize: 14,
                  )),
            ],
          ),
        ),
      ],
    );
  }
}

class _DisclosureCard extends StatelessWidget {
  const _DisclosureCard();

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border.all(color: AppTheme.border),
        borderRadius: BorderRadius.circular(AppTheme.cardRadius),
      ),
      padding: const EdgeInsets.all(14),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Container(
            width: 64,
            height: 64,
            decoration: BoxDecoration(
              color: AppTheme.background,
              borderRadius: BorderRadius.circular(10),
            ),
            child: const Icon(Icons.handshake_outlined,
                color: AppTheme.warning, size: 28),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  s.t('disclosure'),
                  style: const TextStyle(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w700,
                    fontSize: 14,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  s.t('disclosureDesc'),
                  style: const TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                    height: 1.4,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 10),
          ElevatedButton(
            onPressed: () {},
            style: ElevatedButton.styleFrom(
              backgroundColor: AppTheme.primary,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
              minimumSize: const Size(80, 40),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8),
              ),
              elevation: 0,
            ),
            child: Text(
              s.t('disclose'),
              style: const TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.w600,
                fontSize: 12,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
