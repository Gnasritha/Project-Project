import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

/// Horizontal multi-step progress indicator with green completed segments.
class StepProgress extends StatelessWidget {
  final int totalSteps;
  final int currentStep; // 1-based
  final double height;
  final Color activeColor;
  final Color inactiveColor;
  final double spacing;

  const StepProgress({
    super.key,
    required this.totalSteps,
    required this.currentStep,
    this.height = 6,
    this.activeColor = AppTheme.secondary,
    this.inactiveColor = const Color(0xFFD8DDD9),
    this.spacing = 8,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: List.generate(totalSteps, (i) {
        final completed = i < currentStep;
        return Expanded(
          child: Padding(
            padding: EdgeInsetsDirectional.only(end: i == totalSteps - 1 ? 0 : spacing),
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 250),
              height: height,
              decoration: BoxDecoration(
                color: completed ? activeColor : inactiveColor,
                borderRadius: BorderRadius.circular(height / 2),
              ),
            ),
          ),
        );
      }),
    );
  }
}
