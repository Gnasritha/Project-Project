import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../core/localization/app_strings.dart';
import '../theme/app_theme.dart';
import '../widgets/common_buttons.dart';

/// Map location selection sheet.
///
/// Renders a Google Map centred on Riyadh. Tap anywhere on the map to drop the
/// pin; "Confirm" returns the picked coordinates as a "lat, lng" string so the
/// caller (currently FacilityStatusScreen) can stuff it into its location field.
///
/// API key lives in:
///   - web/index.html (Maps JavaScript API)
///   - android/app/src/main/AndroidManifest.xml (Maps SDK for Android)
/// Move to env-driven config + restrict the key in Google Cloud Console before
/// any non-dev deployment.
class MapLocationSheet extends StatefulWidget {
  const MapLocationSheet({super.key});

  @override
  State<MapLocationSheet> createState() => _MapLocationSheetState();
}

class _MapLocationSheetState extends State<MapLocationSheet> {
  // Initial camera target — Kingdom Centre area in central Riyadh, street-level
  // zoom so POIs (Kingdom Centre, Hyatt Regency, etc.) and street labels are
  // visible immediately, matching the standard Google Maps look.
  static const LatLng _initial = LatLng(24.7115, 46.6753);
  static const double _initialZoom = 15.5;

  bool _showWarning = true;
  LatLng? _picked; // null until the user taps the map

  void _onMapTap(LatLng pos) {
    setState(() => _picked = pos);
  }

  void _onConfirm() {
    final p = _picked ?? _initial;
    final value =
        '${p.latitude.toStringAsFixed(6)}, ${p.longitude.toStringAsFixed(6)}';
    Navigator.of(context).pop(value);
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final height = MediaQuery.of(context).size.height * 0.78;
    return SafeArea(
      child: Container(
        height: height,
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
        ),
        child: Column(
          children: [
            const SizedBox(height: 12),
            Container(
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: const Color(0xFFE0E0E0),
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 14, 20, 12),
              child: Row(
                children: [
                  Expanded(
                    child: Text(
                      s.t('selectLocation'),
                      style: const TextStyle(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w700,
                        fontSize: 18,
                      ),
                    ),
                  ),
                  IconButton(
                    onPressed: () => Navigator.of(context).pop(),
                    icon: const Icon(Icons.close, color: AppTheme.textPrimary),
                  ),
                ],
              ),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Stack(
                  children: [
                    ClipRRect(
                      borderRadius: BorderRadius.circular(12),
                      child: GoogleMap(
                        initialCameraPosition: const CameraPosition(
                            target: _initial, zoom: _initialZoom),
                        onTap: _onMapTap,
                        markers: _picked == null
                            ? const <Marker>{}
                            : {
                                Marker(
                                  markerId: const MarkerId('picked'),
                                  position: _picked!,
                                ),
                              },
                        myLocationButtonEnabled: false,
                        zoomControlsEnabled: true,
                        mapToolbarEnabled: false,
                      ),
                    ),
                    if (_showWarning)
                      PositionedDirectional(
                        top: 12,
                        start: 12,
                        end: 12,
                        child: _WarningBanner(
                          message: s.t('mapWarning'),
                          onClose: () => setState(() => _showWarning = false),
                        ),
                      ),
                    if (_picked != null)
                      PositionedDirectional(
                        bottom: 12,
                        start: 12,
                        end: 12,
                        child: _CoordsBadge(latLng: _picked!),
                      ),
                  ],
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 14, 16, 14),
              child: PrimaryButton(
                label: s.t('confirm'),
                onPressed: _onConfirm,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _CoordsBadge extends StatelessWidget {
  final LatLng latLng;
  const _CoordsBadge({required this.latLng});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.92),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: const Color(0xFFE0E0E0)),
      ),
      child: Text(
        '${latLng.latitude.toStringAsFixed(6)}, ${latLng.longitude.toStringAsFixed(6)}',
        textAlign: TextAlign.center,
        style: const TextStyle(
          color: AppTheme.textPrimary,
          fontWeight: FontWeight.w600,
          fontSize: 13,
        ),
      ),
    );
  }
}

class _WarningBanner extends StatelessWidget {
  final String message;
  final VoidCallback onClose;
  const _WarningBanner({required this.message, required this.onClose});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
      decoration: BoxDecoration(
        color: const Color(0xFFFFF8E1),
        border: Border.all(color: const Color(0xFFF5C067)),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        children: [
          const Icon(Icons.warning_amber_rounded,
              color: Color(0xFFF57C00), size: 18),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              message,
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w500,
                fontSize: 12,
              ),
            ),
          ),
          GestureDetector(
            onTap: onClose,
            child: const Icon(Icons.close,
                color: AppTheme.textSecondary, size: 16),
          ),
        ],
      ),
    );
  }
}
