import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import '../core/localization/app_strings.dart';
import '../theme/app_theme.dart';

/// A picked image held in memory before / during upload. On web only [bytes]
/// is populated; on mobile [path] points at the local file.
class PickedImage {
  final String name;
  final String? path;
  final Uint8List? bytes;
  const PickedImage({required this.name, this.path, this.bytes});
}

/// Dashed-border "Upload images here" drop zone. Reused by the
/// Notes & Attachments screen and the Non-compliance reasons sheet.
class ImageUploadBox extends StatelessWidget {
  final VoidCallback onTap;
  final bool busy;

  const ImageUploadBox({super.key, required this.onTap, this.busy = false});

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return GestureDetector(
      onTap: busy ? null : onTap,
      behavior: HitTestBehavior.opaque,
      child: CustomPaint(
        painter: _DashedBorderPainter(),
        child: SizedBox(
          width: double.infinity,
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 26),
            child: Column(
              children: [
                if (busy)
                  const SizedBox(
                    width: 30,
                    height: 30,
                    child: CircularProgressIndicator(
                      strokeWidth: 2.4,
                      valueColor: AlwaysStoppedAnimation(AppTheme.primary),
                    ),
                  )
                else
                  const Icon(Icons.cloud_upload_outlined,
                      color: AppTheme.primary, size: 32),
                const SizedBox(height: 10),
                Text(
                  s.t('uploadImages'),
                  style: const TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

/// Horizontal strip of thumbnails for already-picked images, with a remove
/// button on each tile.
class ImageThumbnailStrip extends StatelessWidget {
  final List<PickedImage> images;
  final void Function(int index)? onRemove;

  const ImageThumbnailStrip({super.key, required this.images, this.onRemove});

  @override
  Widget build(BuildContext context) {
    if (images.isEmpty) return const SizedBox.shrink();
    return SizedBox(
      height: 84,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        itemCount: images.length,
        separatorBuilder: (_, _) => const SizedBox(width: 10),
        itemBuilder: (_, i) {
          final img = images[i];
          return Stack(
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(10),
                child: _thumb(img),
              ),
              if (onRemove != null)
                PositionedDirectional(
                  top: 2,
                  end: 2,
                  child: GestureDetector(
                    onTap: () => onRemove!(i),
                    child: Container(
                      decoration: const BoxDecoration(
                        color: Colors.black54,
                        shape: BoxShape.circle,
                      ),
                      padding: const EdgeInsets.all(2),
                      child: const Icon(Icons.close, size: 14, color: Colors.white),
                    ),
                  ),
                ),
            ],
          );
        },
      ),
    );
  }

  Widget _thumb(PickedImage img) {
    const w = 84.0, h = 84.0;
    if (img.bytes != null) {
      return Image.memory(img.bytes!, width: w, height: h, fit: BoxFit.cover);
    }
    if (!kIsWeb && img.path != null) {
      return Image.file(File(img.path!), width: w, height: h, fit: BoxFit.cover);
    }
    return Container(
      width: w,
      height: h,
      color: AppTheme.border,
      alignment: Alignment.center,
      child: const Icon(Icons.image_outlined, color: AppTheme.textSecondary),
    );
  }
}

class _DashedBorderPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = AppTheme.border
      ..strokeWidth = 1.3
      ..style = PaintingStyle.stroke;
    const dashWidth = 6.0;
    const dashSpace = 4.0;
    final rrect = RRect.fromRectAndRadius(
      Rect.fromLTWH(0, 0, size.width, size.height),
      const Radius.circular(12),
    );
    final path = Path()..addRRect(rrect);
    final dashed = Path();
    for (final metric in path.computeMetrics()) {
      double distance = 0;
      while (distance < metric.length) {
        final next = distance + dashWidth;
        dashed.addPath(
          metric.extractPath(distance, next.clamp(0, metric.length)),
          Offset.zero,
        );
        distance = next + dashSpace;
      }
    }
    canvas.drawPath(dashed, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
