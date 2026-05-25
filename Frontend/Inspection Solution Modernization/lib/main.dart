import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';

import 'core/localization/app_strings.dart';
import 'routes/app_routes.dart';
import 'screens/clause_detail_screen.dart';
import 'screens/compliance_clauses_screen.dart';
import 'screens/dashboard_screen.dart';
import 'screens/facility_status_screen.dart';
import 'screens/login_screen.dart';
import 'screens/notes_attachments_screen.dart';
import 'screens/previous_violations_screen.dart';
import 'screens/review_submit_screen.dart';
import 'screens/splash_screen.dart';
import 'screens/violator_data_screen.dart';
import 'screens/violators_info_screen.dart';
import 'screens/visit_success_screen.dart';
import 'screens/visits_screen.dart';
import 'services/locale_service.dart';
import 'services/session_state.dart';
import 'services/storage_service.dart';
import 'theme/app_theme.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await StorageService.instance.init();

  final localeService = LocaleService();
  await localeService.load();

  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.light,
      statusBarBrightness: Brightness.dark,
    ),
  );

  runApp(MultiProvider(
    providers: [
      ChangeNotifierProvider<LocaleService>.value(value: localeService),
      ChangeNotifierProvider<SessionState>(create: (_) => SessionState()),
    ],
    child: const InspectionApp(),
  ));
}

class InspectionApp extends StatelessWidget {
  const InspectionApp({super.key});

  @override
  Widget build(BuildContext context) {
    final locale = context.watch<LocaleService>().locale;
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Inspection Solution',
      theme: AppTheme.light,
      locale: locale,
      supportedLocales: const [Locale('en'), Locale('ar')],
      localizationsDelegates: const [
        AppStringsDelegate(),
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      builder: (context, child) => child ?? const SizedBox.shrink(),
      initialRoute: Routes.splash,
      onGenerateRoute: (settings) {
        final builder = _routeBuilder(settings.name);
        if (builder == null) return null;
        return PageRouteBuilder(
          settings: settings,
          pageBuilder: (_, _, _) => builder(),
          transitionDuration: const Duration(milliseconds: 280),
          transitionsBuilder: (_, animation, _, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 0.04),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                    parent: animation, curve: Curves.easeOutCubic)),
                child: child,
              ),
            );
          },
        );
      },
    );
  }

  Widget Function()? _routeBuilder(String? name) {
    switch (name) {
      case Routes.splash:
        return () => const SplashScreen();
      case Routes.login:
        return () => const LoginScreen();
      case Routes.dashboard:
        return () => const DashboardScreen();
      case Routes.visits:
        return () => const VisitsScreen();
      case Routes.violatorData:
        return () => const ViolatorDataScreen();
      case Routes.facilityStatus:
        return () => const FacilityStatusScreen();
      case Routes.previousViolations:
        return () => const PreviousViolationsScreen();
      case Routes.complianceClauses:
        return () => const ComplianceClausesScreen();
      case Routes.clauseDetail:
        return () => const ClauseDetailScreen();
      case Routes.violatorsInfo:
        return () => const ViolatorsInfoScreen();
      case Routes.notesAttachments:
        return () => const NotesAttachmentsScreen();
      case Routes.reviewSubmit:
        return () => const ReviewSubmitScreen();
      case Routes.visitSuccess:
        return () => const VisitSuccessScreen();
    }
    return null;
  }
}
