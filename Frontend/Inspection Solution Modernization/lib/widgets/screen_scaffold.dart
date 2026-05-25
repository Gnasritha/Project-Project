import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import 'app_header.dart';
import 'step_progress.dart';

/// Standard inspection-step screen layout: dark green hero header,
/// compact title row with optional INS number + step progress, body content,
/// and a fixed footer for nav buttons.
///
/// All inspector identity / inspection number values come from upstream
/// providers — no hardcoded strings live here.
class InspectionScreenScaffold extends StatelessWidget {
  final String title;
  final String? insNumber;
  final int? totalSteps;
  final int? currentStep;
  final Widget child;
  final Widget? footer;
  final bool showHeader;

  const InspectionScreenScaffold({
    super.key,
    required this.title,
    this.insNumber,
    this.totalSteps,
    this.currentStep,
    required this.child,
    this.footer,
    this.showHeader = true,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: Column(
        children: [
          if (showHeader) const AppHeader(compact: true),
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: const TextStyle(
                          color: AppTheme.textPrimary,
                          fontWeight: FontWeight.w700,
                          fontSize: 20,
                        ),
                      ),
                      if (insNumber != null && insNumber!.isNotEmpty) ...[
                        const SizedBox(height: 4),
                        Text(
                          insNumber!,
                          style: const TextStyle(
                            color: AppTheme.textSecondary,
                            fontSize: 12,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ],
                    ],
                  ),
                ),
                IconButton(
                  onPressed: () {},
                  icon: const Icon(Icons.more_vert, color: AppTheme.textPrimary),
                ),
              ],
            ),
          ),
          if (totalSteps != null && currentStep != null)
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
              child: StepProgress(totalSteps: totalSteps!, currentStep: currentStep!),
            ),
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
              child: child,
            ),
          ),
          ?footer,
        ],
      ),
    );
  }
}
