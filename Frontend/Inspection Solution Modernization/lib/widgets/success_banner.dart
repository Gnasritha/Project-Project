import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

/// Inline "Data saved successfully" banner — green tinted card with a circular
/// check badge and a dismiss button. Rendered within screen/sheet content
/// (not a Material SnackBar) to match the reference design.
class SuccessBanner extends StatelessWidget {
  final String message;
  final VoidCallback? onClose;

  const SuccessBanner({
    super.key,
    required this.message,
    this.onClose,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
      decoration: BoxDecoration(
        color: const Color(0xFFE9F7EF),
        border: Border.all(color: AppTheme.success.withValues(alpha: 0.40)),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        children: [
          Container(
            width: 26,
            height: 26,
            decoration: const BoxDecoration(
              color: AppTheme.success,
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.check, color: Colors.white, size: 16),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              message,
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w700,
                fontSize: 14,
              ),
            ),
          ),
          if (onClose != null)
            GestureDetector(
              onTap: onClose,
              behavior: HitTestBehavior.opaque,
              child: const Icon(Icons.close, size: 18, color: AppTheme.textSecondary),
            ),
        ],
      ),
    );
  }
}
