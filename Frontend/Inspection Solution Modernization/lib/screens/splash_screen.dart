import 'package:flutter/material.dart';
import '../routes/app_routes.dart';
import '../theme/app_theme.dart';
import '../widgets/app_logo.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> with TickerProviderStateMixin {
  late final AnimationController _progress;
  late final AnimationController _fade;

  @override
  void initState() {
    super.initState();
    _fade = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 700),
    )..forward();
    _progress = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 2400),
    )..forward();

    Future.delayed(const Duration(milliseconds: 2700), () {
      if (mounted) Navigator.of(context).pushReplacementNamed(Routes.login);
    });
  }

  @override
  void dispose() {
    _progress.dispose();
    _fade.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: AppTheme.splashGradient),
        child: Stack(
          children: [
            Positioned.fill(
              child: CustomPaint(painter: _SilkPainter()),
            ),
            Center(
              child: FadeTransition(
                opacity: _fade,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const AppLogo(size: 140),
                    const SizedBox(height: 36),
                    AnimatedBuilder(
                      animation: _progress,
                      builder: (_, _) => _progress.value > 0.05
                          ? Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 36),
                              child: Column(
                                children: [
                                  const Text(
                                    'Please wait , preparing the app...',
                                    style: TextStyle(color: Colors.white, fontSize: 14),
                                  ),
                                  const SizedBox(height: 14),
                                  ClipRRect(
                                    borderRadius: BorderRadius.circular(4),
                                    child: LinearProgressIndicator(
                                      value: _progress.value,
                                      minHeight: 4,
                                      backgroundColor: Colors.white24,
                                      valueColor: const AlwaysStoppedAnimation(
                                        AppTheme.secondary,
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            )
                          : const SizedBox.shrink(),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SilkPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()..color = Colors.white.withValues(alpha: 0.04);
    for (var i = 0; i < 6; i++) {
      final dx = size.width * (0.1 + i * 0.18);
      final path = Path()
        ..moveTo(dx, 0)
        ..quadraticBezierTo(
          dx + size.width * 0.05,
          size.height * 0.4,
          dx - size.width * 0.05,
          size.height,
        )
        ..lineTo(dx + 40, size.height)
        ..quadraticBezierTo(dx, size.height * 0.4, dx + 40, 0)
        ..close();
      canvas.drawPath(path, paint);
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
