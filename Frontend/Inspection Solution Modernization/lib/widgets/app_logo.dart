import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

/// Reusable Mumtethel-style logo: Arabic word "ممثل" with a checkmark glyph.
class AppLogo extends StatelessWidget {
  final double size;
  final Color color;
  final Color accentColor;
  final bool showSubtitle;

  const AppLogo({
    super.key,
    this.size = 120,
    this.color = Colors.white,
    this.accentColor = AppTheme.secondary,
    this.showSubtitle = true,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        SizedBox(
          width: size,
          height: size * 0.7,
          child: Stack(
            alignment: Alignment.center,
            children: [
              Positioned(
                right: size * 0.05,
                top: size * 0.05,
                child: SizedBox(
                  width: size * 0.45,
                  height: size * 0.45,
                  child: CustomPaint(
                    painter: _CheckArcPainter(color: color, accent: accentColor),
                  ),
                ),
              ),
              Positioned(
                left: size * 0.05,
                top: size * 0.18,
                child: Text(
                  'ممثل',
                  style: TextStyle(
                    color: color,
                    fontSize: size * 0.28,
                    fontWeight: FontWeight.w700,
                    height: 1,
                  ),
                ),
              ),
            ],
          ),
        ),
        if (showSubtitle) ...[
          const SizedBox(height: 6),
          Text(
            'برنامج التفتيش الميداني',
            style: TextStyle(
              color: color.withValues(alpha: 0.8),
              fontSize: size * 0.085,
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ],
    );
  }
}

class _CheckArcPainter extends CustomPainter {
  final Color color;
  final Color accent;
  _CheckArcPainter({required this.color, required this.accent});

  @override
  void paint(Canvas canvas, Size size) {
    final ringPaint = Paint()
      ..color = color
      ..style = PaintingStyle.stroke
      ..strokeWidth = size.width * 0.06
      ..strokeCap = StrokeCap.round;

    // Three-quarter arc
    canvas.drawArc(
      Rect.fromLTWH(0, 0, size.width, size.height),
      -1.2,
      4.8,
      false,
      ringPaint,
    );

    // Check mark
    final checkPaint = Paint()
      ..color = accent
      ..style = PaintingStyle.stroke
      ..strokeWidth = size.width * 0.10
      ..strokeCap = StrokeCap.round
      ..strokeJoin = StrokeJoin.round;
    final path = Path()
      ..moveTo(size.width * 0.28, size.height * 0.55)
      ..lineTo(size.width * 0.45, size.height * 0.72)
      ..lineTo(size.width * 0.78, size.height * 0.32);
    canvas.drawPath(path, checkPaint);
  }

  @override
  bool shouldRepaint(covariant _CheckArcPainter oldDelegate) =>
      oldDelegate.color != color || oldDelegate.accent != accent;
}
