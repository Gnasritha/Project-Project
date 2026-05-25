import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import '../core/api/api_exception.dart';
import '../core/localization/app_strings.dart';
import '../data/models/attachment_models.dart';
import '../data/models/isic_models.dart';
import '../routes/app_routes.dart';
import '../services/attachment_service.dart';
import '../services/isic_service.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';
import '../utils/extensions.dart';
import '../widgets/common_buttons.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/screen_scaffold.dart';

enum _FacilityState { open, closed, notKnown }

class FacilityStatusScreen extends StatefulWidget {
  const FacilityStatusScreen({super.key});

  @override
  State<FacilityStatusScreen> createState() => _FacilityStatusScreenState();
}

class _FacilityStatusScreenState extends State<FacilityStatusScreen> {
  _FacilityState _state = _FacilityState.open;
  final _location = TextEditingController();
  final _arabicName = TextEditingController();
  final _englishName = TextEditingController();

  // ISIC
  Future<List<IsicActivity>>? _isicTopFuture;
  IsicActivity? _selectedCategory;
  IsicDetailActivity? _selectedDetail;
  Future<List<IsicDetailActivity>>? _detailFuture;

  // Attachments
  final List<_PickedImage> _localImages = [];
  final List<AttachmentDto> _uploaded = [];
  bool _uploading = false;

  @override
  void initState() {
    super.initState();
    _isicTopFuture = IsicService.instance.fetchTopLevel();
    _loadExistingAttachments();
    // No-license flow: pre-fill Location with the map-picked coordinates.
    final picked = context.read<SessionState>().pickedLocation;
    if (picked != null && picked.isNotEmpty) {
      _location.text = picked;
    }
  }

  Future<void> _loadExistingAttachments() async {
    final id = context.read<SessionState>().inspectionId;
    if (id == null) return;
    try {
      final list = await AttachmentService.instance.list(id);
      if (!mounted) return;
      setState(() {
        _uploaded
          ..clear()
          ..addAll(list);
      });
    } catch (_) {/* ignore — list is empty */}
  }

  @override
  void dispose() {
    _location.dispose();
    _arabicName.dispose();
    _englishName.dispose();
    super.dispose();
  }

  Future<void> _pickImage() async {
    final id = context.read<SessionState>().inspectionId;
    try {
      final picker = ImagePicker();
      final picked = await picker.pickImage(source: ImageSource.gallery);
      if (picked == null) return;

      final bytes = kIsWeb ? await picked.readAsBytes() : null;
      final entry = _PickedImage(name: picked.name, bytes: bytes, path: picked.path);
      setState(() {
        _localImages.add(entry);
        _uploading = true;
      });

      if (id != null) {
        try {
          final att = await AttachmentService.instance.upload(
            id,
            filePath: kIsWeb ? null : picked.path,
            bytes: bytes,
            filename: picked.name,
            mimeType: picked.mimeType,
          );
          if (!mounted) return;
          setState(() => _uploaded.add(att));
          context.showSnack('Attachment uploaded');
        } on ApiException catch (e) {
          if (!mounted) return;
          context.showSnack(e.message, color: AppTheme.error);
        }
      }
    } catch (_) {
      // image picker may be unavailable
    } finally {
      if (mounted) setState(() => _uploading = false);
    }
  }

  void _onCategoryChanged(IsicActivity? category) {
    setState(() {
      _selectedCategory = category;
      _selectedDetail = null;
      _detailFuture = category == null
          ? null
          : IsicService.instance.fetchDetails(category.id);
    });
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final session = context.watch<SessionState>();
    return InspectionScreenScaffold(
      title: s.t('facilityStatus'),
      insNumber: session.caseDisplayId,
      totalSteps: 6,
      currentStep: 2,
      footer: FooterNavBar(
        previousLabel: s.t('previous'),
        nextLabel: s.t('next'),
        onPrevious: () => Navigator.of(context).pop(),
        onNext: () => Navigator.of(context).pushNamed(Routes.previousViolations),
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
                  label: s.t('open'),
                  active: _state == _FacilityState.open,
                  activeColor: AppTheme.secondary,
                  onTap: () => setState(() => _state = _FacilityState.open),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: StatusChip(
                  label: s.t('closed'),
                  active: _state == _FacilityState.closed,
                  activeColor: AppTheme.primary,
                  onTap: () => setState(() => _state = _FacilityState.closed),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: StatusChip(
                  label: s.t('notKnown'),
                  active: _state == _FacilityState.notKnown,
                  activeColor: AppTheme.primary,
                  onTap: () => setState(() => _state = _FacilityState.notKnown),
                ),
              ),
            ],
          ),
          const SizedBox(height: 18),
          // Location is picked on the Violator Data map and carried here —
          // read-only, no map opens from this screen.
          CustomTextField(
            label: s.t('location'),
            hint: s.t('enterLocation'),
            controller: _location,
            readOnly: true,
            suffixIcon: const Padding(
              padding: EdgeInsetsDirectional.only(end: 12),
              child: Icon(Icons.location_on_outlined,
                  color: AppTheme.textSecondary, size: 20),
            ),
          ),
          const SizedBox(height: 16),
          CustomTextField(
            label: s.t('facilityNameAr'),
            hint: s.t('enterArabicName'),
            controller: _arabicName,
          ),
          const SizedBox(height: 16),
          CustomTextField(
            label: s.t('facilityNameEn'),
            hint: s.t('enterEnglishName'),
            controller: _englishName,
          ),
          const SizedBox(height: 16),
          Text(
            s.t('photosOutside'),
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 8),
          GestureDetector(
            onTap: _uploading ? null : _pickImage,
            child: DottedBorderBox(
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 22),
                child: Column(
                  children: [
                    Container(
                      decoration: BoxDecoration(
                        color: AppTheme.background,
                        borderRadius: BorderRadius.circular(12),
                      ),
                      padding: const EdgeInsets.all(10),
                      child: _uploading
                          ? const SizedBox(
                              width: 28,
                              height: 28,
                              child: CircularProgressIndicator(
                                color: AppTheme.primary,
                                strokeWidth: 2,
                              ),
                            )
                          : const Icon(Icons.cloud_upload_outlined,
                              color: AppTheme.primary, size: 28),
                    ),
                    const SizedBox(height: 10),
                    Text(
                      s.t('uploadImages'),
                      style: const TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 13,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          if (_localImages.isNotEmpty) ...[
            const SizedBox(height: 12),
            SizedBox(
              height: 80,
              child: ListView.separated(
                scrollDirection: Axis.horizontal,
                itemCount: _localImages.length,
                separatorBuilder: (_, _) => const SizedBox(width: 10),
                itemBuilder: (_, i) {
                  final img = _localImages[i];
                  return ClipRRect(
                    borderRadius: BorderRadius.circular(10),
                    child: img.bytes != null
                        ? Image.memory(Uint8List.fromList(img.bytes!),
                            width: 80, height: 80, fit: BoxFit.cover)
                        : Image.file(File(img.path),
                            width: 80, height: 80, fit: BoxFit.cover),
                  );
                },
              ),
            ),
          ],
          const SizedBox(height: 18),
          FutureBuilder<List<IsicActivity>>(
            future: _isicTopFuture,
            builder: (_, snap) {
              final loading = snap.connectionState != ConnectionState.done;
              final items = snap.data ?? const <IsicActivity>[];
              return CustomDropdownField<IsicActivity>(
                label: s.t('isicActivity'),
                value: _selectedCategory,
                items: items
                    .map((a) => DropdownMenuItem(
                          value: a,
                          child: Text(s.isAr ? (a.nameAr ?? a.name) : a.name),
                        ))
                    .toList(),
                hint: loading ? 'Loading…' : 'Select category',
                onChanged: _onCategoryChanged,
              );
            },
          ),
          const SizedBox(height: 16),
          FutureBuilder<List<IsicDetailActivity>>(
            future: _detailFuture,
            builder: (_, snap) {
              final loading =
                  _detailFuture != null && snap.connectionState != ConnectionState.done;
              final items = snap.data ?? const <IsicDetailActivity>[];
              return CustomDropdownField<IsicDetailActivity>(
                label: s.t('detailedActivity'),
                value: _selectedDetail,
                items: items
                    .map((d) => DropdownMenuItem(
                          value: d,
                          child: Text(s.isAr ? (d.nameAr ?? d.name) : d.name),
                        ))
                    .toList(),
                hint: _detailFuture == null
                    ? 'Pick a category first'
                    : (loading ? 'Loading…' : 'Select detail'),
                onChanged: (v) => setState(() => _selectedDetail = v),
              );
            },
          ),
          if (_uploaded.isNotEmpty) ...[
            const SizedBox(height: 18),
            Text(
              'Uploaded files (${_uploaded.length})',
              style: const TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 12,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ],
      ),
    );
  }
}

class _PickedImage {
  final String name;
  final List<int>? bytes;
  final String path;
  _PickedImage({required this.name, required this.bytes, required this.path});
}

class DottedBorderBox extends StatelessWidget {
  final Widget child;
  const DottedBorderBox({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return CustomPaint(
      painter: _DashedBorderPainter(),
      child: SizedBox(width: double.infinity, child: child),
    );
  }
}

class _DashedBorderPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = AppTheme.border
      ..strokeWidth = 1.2
      ..style = PaintingStyle.stroke;
    const dashWidth = 6.0;
    const dashSpace = 4.0;
    final radius = const Radius.circular(12);
    final rect = Rect.fromLTWH(0, 0, size.width, size.height);
    final rrect = RRect.fromRectAndRadius(rect, radius);

    final path = Path()..addRRect(rrect);
    final dashPath = Path();
    for (final metric in path.computeMetrics()) {
      double distance = 0;
      while (distance < metric.length) {
        final next = distance + dashWidth;
        dashPath.addPath(
          metric.extractPath(distance, next.clamp(0, metric.length)),
          Offset.zero,
        );
        distance = next + dashSpace;
      }
    }
    canvas.drawPath(dashPath, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
