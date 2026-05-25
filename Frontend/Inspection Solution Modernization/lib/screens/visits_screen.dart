import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/localization/app_strings.dart';
import '../data/models/visit_dtos.dart';
import '../routes/app_routes.dart';
import '../services/session_state.dart';
import '../services/visit_service.dart';
import '../theme/app_theme.dart';
import '../widgets/app_bottom_nav.dart';
import '../widgets/app_header.dart';
import '../widgets/common_buttons.dart';

class VisitsScreen extends StatefulWidget {
  const VisitsScreen({super.key});

  @override
  State<VisitsScreen> createState() => _VisitsScreenState();
}

class _VisitsScreenState extends State<VisitsScreen> {
  int _navIndex = 1;

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: Stack(
        children: [
          Column(
            children: [
              const AppHeader(compact: true),
              Expanded(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.fromLTRB(16, 18, 16, 24),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            s.t('visitsList'),
                            style: const TextStyle(
                              color: AppTheme.textPrimary,
                              fontSize: 20,
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                          IconButton(
                            onPressed: () => _openControlTypeSheet(context),
                            icon: const Icon(Icons.add,
                                color: AppTheme.textPrimary, size: 24),
                          ),
                        ],
                      ),
                      const SizedBox(height: 32),
                      Center(
                        child: Container(
                          width: 220,
                          height: 220,
                          decoration: BoxDecoration(
                            color: const Color(0xFFEFF3EE),
                            borderRadius: BorderRadius.circular(28),
                          ),
                          child: const Center(
                            child: Icon(Icons.folder_open_outlined,
                                size: 96, color: AppTheme.primary),
                          ),
                        ),
                      ),
                      const SizedBox(height: 30),
                      Center(
                        child: Text(
                          s.t('noVisitsToday'),
                          style: const TextStyle(
                            color: AppTheme.textPrimary,
                            fontWeight: FontWeight.w700,
                            fontSize: 18,
                          ),
                        ),
                      ),
                      const SizedBox(height: 10),
                      Center(
                        child: Text(
                          s.t('contactSupervisor'),
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            color: AppTheme.textSecondary,
                            fontSize: 13,
                          ),
                        ),
                      ),
                      const SizedBox(height: 26),
                      PrimaryButton(
                        label: s.t('createVisit'),
                        onPressed: () => _openControlTypeSheet(context),
                      ),
                      const SizedBox(height: 100),
                    ],
                  ),
                ),
              ),
            ],
          ),
          PositionedDirectional(
            bottom: 24,
            end: 16,
            child: Material(
              color: AppTheme.primary,
              shape: const CircleBorder(),
              elevation: 6,
              child: InkWell(
                customBorder: const CircleBorder(),
                onTap: () => Navigator.of(context).pushNamed(Routes.facilityStatus),
                child: const SizedBox(
                  width: 52,
                  height: 52,
                  child: Icon(Icons.map_outlined, color: Colors.white),
                ),
              ),
            ),
          ),
        ],
      ),
      bottomNavigationBar: AppBottomNav(
        currentIndex: _navIndex,
        onTap: (i) {
          setState(() => _navIndex = i);
          if (i == 0) Navigator.of(context).pop();
        },
      ),
    );
  }

  void _openControlTypeSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => const SelectControlTypeSheet(),
    );
  }
}

class SelectControlTypeSheet extends StatefulWidget {
  const SelectControlTypeSheet({super.key});

  @override
  State<SelectControlTypeSheet> createState() => _SelectControlTypeSheetState();
}

class _SelectControlTypeSheetState extends State<SelectControlTypeSheet> {
  late Future<List<InspectionType>> _future;
  int _selected = 0;

  @override
  void initState() {
    super.initState();
    final inspectorId = context.read<SessionState>().inspectorId;
    _future = VisitService.instance.fetchInspectionTypes(inspectorId);
  }

  IconData _iconFor(InspectionType t) {
    final name = (t.name).toLowerCase();
    if (name.contains('housing') || (t.nameAr ?? '').contains('السكن')) {
      return Icons.apartment_outlined;
    }
    if (name.contains('market') || (t.nameAr ?? '').contains('الأسواق')) {
      return Icons.shopping_cart_outlined;
    }
    if (name.contains('health') || (t.nameAr ?? '').contains('الصحية')) {
      return Icons.health_and_safety_outlined;
    }
    return Icons.assignment_outlined;
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final maxSheetHeight = MediaQuery.of(context).size.height * 0.85;
    return SafeArea(
      child: Container(
        constraints: BoxConstraints(maxHeight: maxSheetHeight),
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
        ),
        padding: const EdgeInsets.fromLTRB(20, 14, 20, 20),
        child: FutureBuilder<List<InspectionType>>(
          future: _future,
          builder: (_, snap) {
            final loading = snap.connectionState != ConnectionState.done;
            final hasError = snap.hasError;
            final types = snap.data ?? const <InspectionType>[];
            return Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: const Color(0xFFE0E0E0),
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
                const SizedBox(height: 14),
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        s.t('selectControlType'),
                        style: const TextStyle(
                          color: AppTheme.textPrimary,
                          fontSize: 18,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                    ),
                    IconButton(
                      onPressed: () => Navigator.of(context).pop(),
                      icon: const Icon(Icons.close, color: AppTheme.textPrimary),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                Flexible(
                  child: Builder(builder: (_) {
                    if (loading) {
                      return const Padding(
                        padding: EdgeInsets.symmetric(vertical: 24),
                        child: CircularProgressIndicator(color: AppTheme.primary),
                      );
                    }
                    if (hasError) {
                      return Padding(
                        padding: const EdgeInsets.symmetric(vertical: 16),
                        child: Text(
                          '${snap.error}',
                          textAlign: TextAlign.center,
                          style: const TextStyle(color: AppTheme.error, fontSize: 13),
                        ),
                      );
                    }
                    if (types.isEmpty) {
                      return const Padding(
                        padding: EdgeInsets.symmetric(vertical: 16),
                        child: Text(
                          'No control types assigned',
                          style: TextStyle(color: AppTheme.textSecondary, fontSize: 13),
                        ),
                      );
                    }
                    return ListView.separated(
                      shrinkWrap: true,
                      itemCount: types.length,
                      separatorBuilder: (_, _) => const SizedBox(height: 10),
                      itemBuilder: (_, i) {
                        final t = types[i];
                        final selected = _selected == i;
                        final label = (s.isAr && (t.nameAr ?? '').isNotEmpty)
                            ? t.nameAr!
                            : (t.name.isNotEmpty ? t.name : 'Type #${t.id}');
                        return GestureDetector(
                          onTap: () => setState(() => _selected = i),
                          child: AnimatedContainer(
                            duration: const Duration(milliseconds: 180),
                            padding: const EdgeInsets.symmetric(
                                horizontal: 14, vertical: 14),
                            decoration: BoxDecoration(
                              color: selected
                                  ? const Color(0xFFEFF3EE)
                                  : Colors.white,
                              border: Border.all(
                                color: selected ? AppTheme.primary : AppTheme.border,
                              ),
                              borderRadius: BorderRadius.circular(AppTheme.cardRadius),
                            ),
                            child: Row(
                              children: [
                                Icon(_iconFor(t),
                                    color: AppTheme.textPrimary, size: 22),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: Text(
                                    label,
                                    maxLines: 2,
                                    overflow: TextOverflow.ellipsis,
                                    style: const TextStyle(
                                      color: AppTheme.textPrimary,
                                      fontWeight: FontWeight.w600,
                                      fontSize: 14,
                                    ),
                                  ),
                                ),
                                if (selected)
                                  const Icon(Icons.check_circle,
                                      color: AppTheme.primary, size: 20),
                              ],
                            ),
                          ),
                        );
                      },
                    );
                  }),
                ),
                const SizedBox(height: 16),
                PrimaryButton(
                  label: s.t('startVisit'),
                  onPressed: types.isEmpty
                      ? null
                      : () {
                          context.read<SessionState>().setSelectedType(
                                types[_selected.clamp(0, types.length - 1)],
                              );
                          Navigator.of(context).pop();
                          Navigator.of(context).pushNamed(Routes.violatorData);
                        },
                ),
              ],
            );
          },
        ),
      ),
    );
  }
}

