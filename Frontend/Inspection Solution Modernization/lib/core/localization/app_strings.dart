import 'package:flutter/material.dart';

/// Lightweight in-app localization. Two-language support: en / ar.
/// For real i18n at scale, migrate to flutter gen-l10n or intl_translation.
class AppStrings {
  final Locale locale;
  AppStrings(this.locale);

  static AppStrings of(BuildContext context) =>
      Localizations.of<AppStrings>(context, AppStrings) ?? AppStrings(const Locale('en'));

  bool get isAr => locale.languageCode == 'ar';

  static const Map<String, Map<String, String>> _values = {
    // Common
    'next': {'en': 'Next', 'ar': 'التالي'},
    'previous': {'en': 'Previous', 'ar': 'السابق'},
    'save': {'en': 'Save', 'ar': 'حفظ'},
    'cancel': {'en': 'Cancel', 'ar': 'إلغاء'},
    'confirm': {'en': 'Confirm Location', 'ar': 'تأكيد الموقع'},

    // Splash
    'preparing': {'en': 'Please wait , preparing the app...', 'ar': 'يرجى الانتظار، يتم تجهيز التطبيق...'},

    // Token
    'generateToken': {'en': 'Generate Token', 'ar': 'إنشاء رمز'},
    'generateTokenDesc': {
      'en': 'Generate a one-time token to log in to the application.',
      'ar': 'قم بإنشاء رمز لمرة واحدة لتسجيل الدخول إلى التطبيق.',
    },
    'idOrMobile': {'en': 'ID Number / Mobile', 'ar': 'رقم الهوية / الجوال'},
    'enterIdMobile': {'en': 'Enter ID Number/ Mobile', 'ar': 'أدخل رقم الهوية / الجوال'},
    'token': {'en': 'Token', 'ar': 'الرمز'},
    'copyToken': {'en': 'Copy Token', 'ar': 'نسخ الرمز'},
    'continueLogin': {'en': 'Continue to login', 'ar': 'متابعة تسجيل الدخول'},

    // Login
    'loginTitle': {'en': 'National Platform for Control and Inspection', 'ar': 'المنصة الوطنية للرقابة والتفتيش'},
    'password': {'en': 'Password', 'ar': 'كلمة المرور'},
    'enterPassword': {'en': 'Enter Password', 'ar': 'أدخل كلمة المرور'},
    'rememberMe': {'en': 'Remember Me', 'ar': 'تذكرني'},
    'forgotPassword': {'en': 'Forgot Password', 'ar': 'نسيت كلمة المرور'},
    'login': {'en': 'Login', 'ar': 'تسجيل الدخول'},
    'or': {'en': 'OR', 'ar': 'أو'},
    'loginNafath': {'en': 'Login via Nafath', 'ar': 'تسجيل الدخول عبر نفاذ'},
    'noAccount': {'en': 'Don’t have an account?', 'ar': 'ليس لديك حساب؟'},
    'createAccount': {'en': 'Create a New Account', 'ar': 'إنشاء حساب جديد'},

    // Dashboard
    'goodMorning': {'en': 'Good morning!', 'ar': 'صباح الخير!'},
    'goodAfternoon': {'en': 'Good afternoon!', 'ar': 'طاب يومك!'},
    'goodEvening': {'en': 'Good evening!', 'ar': 'مساء الخير!'},
    'platformTagline': {
      'en': 'The National Inspection and Control Platform. Innovation and sustainability!',
      'ar': 'المنصة الوطنية للتفتيش والرقابة. الابتكار والاستدامة!',
    },
    'fieldInspector': {'en': 'Field Inspector', 'ar': 'مفتش ميداني'},
    'dailyReport': {'en': 'Daily Report', 'ar': 'التقرير اليومي'},
    'all': {'en': 'All', 'ar': 'الكل'},
    'assignedVisits': {'en': 'Assigned visits', 'ar': 'الزيارات المخصصة'},
    'completedVisits': {'en': 'Completed visits', 'ar': 'الزيارات المكتملة'},
    'unscheduledVisits': {'en': 'Unscheduled visits', 'ar': 'الزيارات غير المجدولة'},
    'timeSpent': {'en': 'Time spent on visits', 'ar': 'الوقت المستغرق'},
    'nonCompliant': {'en': 'Non-compliant clauses', 'ar': 'البنود غير الممتثلة'},
    'noTasks': {'en': 'No tasks available', 'ar': 'لا توجد مهام متاحة'},
    'dashboard': {'en': 'Dashboard', 'ar': 'الرئيسية'},
    'visits': {'en': 'Visits', 'ar': 'الزيارات'},
    'tools': {'en': 'Tools', 'ar': 'الأدوات'},
    'account': {'en': 'Account', 'ar': 'الحساب'},

    // Visits
    'visitsList': {'en': 'Visits List', 'ar': 'قائمة الزيارات'},
    'noVisitsToday': {'en': 'You have no visits today', 'ar': 'ليس لديك زيارات اليوم'},
    'contactSupervisor': {
      'en': 'Contact your supervisor or start unscheduled visits',
      'ar': 'اتصل بالمشرف أو ابدأ زيارة غير مجدولة',
    },
    'createVisit': {'en': 'Create a Visit', 'ar': 'إنشاء زيارة'},
    'selectControlType': {'en': 'Select Control Type', 'ar': 'اختر نوع الرقابة'},
    'startVisit': {'en': 'Start Visit', 'ar': 'بدء الزيارة'},

    // Violator data
    'violatorData': {'en': 'Violator data', 'ar': 'بيانات المخالف'},
    'isThereLicense': {'en': 'Is there a license?', 'ar': 'هل يوجد رخصة؟'},
    'yesLicense': {'en': 'Yes, there is a license', 'ar': 'نعم، يوجد رخصة'},
    'noLicense': {'en': 'No, there is no license', 'ar': 'لا، لا يوجد رخصة'},
    'addLicenseData': {'en': 'Add license data', 'ar': 'أضف بيانات الرخصة'},
    'defineBasedOn': {'en': 'Define based on', 'ar': 'حدد بناءً على'},
    'commercialLicense': {'en': 'Commercial License', 'ar': 'رخصة تجارية'},
    'access': {'en': 'Access', 'ar': 'الوصول'},
    'licenseNumber': {'en': 'License number', 'ar': 'رقم الرخصة'},
    'selectFromMap': {'en': 'Select from the map', 'ar': 'اختر من الخريطة'},
    'verify': {'en': 'Verify', 'ar': 'تحقق'},
    'noDataFound': {'en': 'No data found for the values you submitted', 'ar': 'لم يتم العثور على بيانات للقيم المُدخلة'},
    'disclosure': {'en': 'Disclosure', 'ar': 'إفصاح'},
    'disclosureDesc': {
      'en': 'Do you have a family relationship with the establishment’s owners',
      'ar': 'هل لديك صلة قرابة بأصحاب المنشأة',
    },
    'disclose': {'en': 'Disclose n...', 'ar': 'إفصاح'},
    'licenseStatus': {'en': 'License status', 'ar': 'حالة الرخصة'},
    'licenseType': {'en': 'License type', 'ar': 'نوع الرخصة'},
    'mobileNumber': {'en': 'Mobile number', 'ar': 'رقم الجوال'},
    'moreDetails': {'en': 'More Details', 'ar': 'مزيد من التفاصيل'},

    // Facility status
    'facilityStatus': {'en': 'Facility status', 'ar': 'حالة المنشأة'},
    'open': {'en': 'Open', 'ar': 'مفتوح'},
    'closed': {'en': 'Closed', 'ar': 'مغلق'},
    'notKnown': {'en': 'Not Known', 'ar': 'غير معروف'},
    'location': {'en': 'Location', 'ar': 'الموقع'},
    'enterLocation': {'en': 'Enter Location', 'ar': 'أدخل الموقع'},
    'facilityNameAr': {'en': 'Facility name (Arabic)', 'ar': 'اسم المنشأة (عربي)'},
    'facilityNameEn': {'en': 'Facility name (English)', 'ar': 'اسم المنشأة (إنجليزي)'},
    'enterArabicName': {'en': 'Enter the Arabic Name', 'ar': 'أدخل الاسم بالعربية'},
    'enterEnglishName': {'en': 'Enter the English Name', 'ar': 'أدخل الاسم بالإنجليزية'},
    'photosOutside': {'en': 'Photos of the shop from outside', 'ar': 'صور المحل من الخارج'},
    'uploadImages': {'en': 'Upload images here', 'ar': 'ارفع الصور هنا'},
    'isicActivity': {'en': 'ISIC activity', 'ar': 'النشاط حسب التصنيف الدولي'},
    'detailedActivity': {'en': 'Detailed activity', 'ar': 'النشاط التفصيلي'},
    'isicSample': {'en': 'Wholesale of fruit', 'ar': 'البيع بالجملة للفواكه'},
    'selectLocation': {'en': 'Select location', 'ar': 'اختر الموقع'},
    'mapWarning': {
      'en': 'The chosen location must be within 200.0 meters',
      'ar': 'يجب أن يكون الموقع المختار في حدود 200 متر',
    },

    // Previous violations
    'previousViolations': {'en': 'Previous violations', 'ar': 'المخالفات السابقة'},
    'needsDecision': {'en': 'Needs decision', 'ar': 'يحتاج قرار'},
    'fixed': {'en': 'Fixed', 'ar': 'تم التصحيح'},
    'violationDate': {'en': 'Violation Date', 'ar': 'تاريخ المخالفة'},

    // Compliance
    'complianceClauses': {'en': 'Compliance clauses', 'ar': 'بنود الامتثال'},
    'allCompliant': {'en': 'All clauses are compliant', 'ar': 'كل البنود ممتثلة'},
    'allClauses': {'en': 'All clauses', 'ar': 'كل البنود'},
    'compliant': {'en': 'Compliant', 'ar': 'ممتثل'},
    'nonCompliantOpt': {'en': 'Non-Compliant', 'ar': 'غير ممتثل'},
    'notApplicable': {'en': 'Not Applicable', 'ar': 'لا ينطبق'},
    'low': {'en': 'LOW', 'ar': 'منخفض'},
    'medium': {'en': 'MED', 'ar': 'متوسط'},
    'high': {'en': 'HIGH', 'ar': 'مرتفع'},
    'enterReason': {'en': 'Enter Reason for Non - Compliance', 'ar': 'أدخل سبب عدم الامتثال'},
    'clauseDetailTitle': {'en': 'License & activity violations', 'ar': 'مخالفات الرخصة و مزاولة النشاط'},

    // ───────── Sprint 2 — inspection workflow ─────────
    'dataSavedSuccessfully': {'en': 'Data saved successfully', 'ar': 'تم حفظ البيانات بنجاح'},
    'generalClauses': {'en': 'General clauses', 'ar': 'البنود العامة'},

    // Non-compliance reasons sheet
    'nonComplianceReasons': {'en': 'Non-compliance reasons', 'ar': 'أسباب عدم الامتثال'},
    'clause': {'en': 'Clause', 'ar': 'البند'},
    'reasonForNonCompliance': {'en': 'Reason for non-compliance', 'ar': 'سبب عدم الامتثال'},
    'otherReason': {'en': 'Other reason', 'ar': 'سبب آخر'},
    'enterReasonShort': {'en': 'Enter Reason', 'ar': 'أدخل السبب'},
    'selectViolationsAndDetails': {'en': 'Select violations and add details', 'ar': 'اختر المخالفات وأضف التفاصيل'},
    'addViolation': {'en': 'Add Violation', 'ar': 'إضافة مخالفة'},
    'inspectorNotes': {'en': "Inspector's notes for reviewer/approver", 'ar': 'ملاحظات المفتش للمراجع/المعتمد'},
    'notes': {'en': 'Notes', 'ar': 'ملاحظات'},
    'imagesAndAttachments': {'en': 'Images and attachments', 'ar': 'الصور والمرفقات'},
    'add': {'en': 'Add', 'ar': 'إضافة'},
    'editViolation': {'en': 'Edit violation', 'ar': 'تعديل المخالفة'},
    'offender': {'en': 'Offender', 'ar': 'المخالف'},
    'penalty': {'en': 'Penalty', 'ar': 'العقوبة'},

    // Specify violations sheet
    'specifyViolations': {'en': 'Specify violations', 'ar': 'تحديد المخالفات'},
    'enterViolationDetails': {'en': 'Enter Violation Details', 'ar': 'إدخال تفاصيل المخالفة'},

    // Violation details sheet
    'violationsDetails': {'en': 'Violations details', 'ar': 'تفاصيل المخالفات'},
    'violationCode': {'en': 'Violation code', 'ar': 'رمز المخالفة'},
    'numberOfUnits': {'en': 'Number of units', 'ar': 'عدد الوحدات'},
    'numberOfUnitsHint': {'en': '( for the shop / facility )', 'ar': '( للمحل / للمنشأة )'},
    'violator': {'en': 'Violator', 'ar': 'مخالف'},
    'contractor': {'en': 'Contractor', 'ar': 'المقاول'},
    'select': {'en': 'Select', 'ar': 'اختر'},
    'searchHere': {'en': 'Search here', 'ar': 'ابحث هنا'},
    'enterValue': {'en': 'Enter', 'ar': 'أدخل'},
    'subsequentPenalties': {'en': 'Subsequent penalties', 'ar': 'العقوبات اللاحقة'},
    'addProduct': {'en': 'Add product', 'ar': 'إضافة منتج'},
    'consequencesQuestion': {
      'en': 'Are there consequences resulting from the violation?',
      'ar': 'هل هناك آثار مترتبة على المخالفة؟',
    },
    'yesThereAre': {'en': 'Yes, there are', 'ar': 'نعم، يوجد'},
    'noThereAreNot': {'en': 'No, there are not', 'ar': 'لا، لا يوجد'},
    'violatorPresenceStatus': {'en': 'Violator presence status', 'ar': 'حالة حضور المخالف'},
    'presentCooperative': {'en': 'Present and cooperative', 'ar': 'حاضر ومتعاون'},
    'presentUncooperative': {'en': 'Present and uncooperative', 'ar': 'حاضر وغير متعاون'},
    'absent': {'en': 'Absent', 'ar': 'غائب'},
    'requiredAction': {'en': 'Required Action', 'ar': 'الإجراء المطلوب'},

    // Violators info screen
    'violatorsInfo': {'en': 'Violators info', 'ar': 'بيانات المخالفين'},
    'addViolator': {'en': 'Add Violator', 'ar': 'إضافة مخالف'},
    'violationClauses': {'en': 'violation clauses', 'ar': 'بنود مخالفة'},
    'isViolatorIdentified': {'en': 'Is the violator identified?', 'ar': 'هل تم تحديد المخالف؟'},
    'yesIdentified': {'en': 'Yes, identified', 'ar': 'نعم، تم التحديد'},
    'noCouldntIdentify': {'en': "No, couldn't identify", 'ar': 'لا، تعذّر التحديد'},
    'fillViolatorData': {'en': 'Fill Violator Data', 'ar': 'تعبئة بيانات المخالف'},
    'editViolatorData': {'en': 'Edit violator data', 'ar': 'تعديل بيانات المخالف'},
    'owner': {'en': 'Owner', 'ar': 'المالك'},
    'nationalFacilityNumber': {'en': 'National facility number', 'ar': 'الرقم الوطني للمنشأة'},

    // Violator data sheet
    'fillData': {'en': 'Fill in data', 'ar': 'تعبئة البيانات'},
    'violatorCategory': {'en': 'Violator Category', 'ar': 'فئة المخالف'},
    'entity': {'en': 'Entity', 'ar': 'منشأة'},
    'individual': {'en': 'Individual', 'ar': 'فرد'},
    'idNumber': {'en': 'ID number', 'ar': 'رقم الهوية'},
    'birthDate': {'en': 'Birth date', 'ar': 'تاريخ الميلاد'},
    'checkEnteredData': {'en': 'Check the entered data', 'ar': 'تحقق من البيانات المُدخلة'},
    'verified': {'en': 'Verified', 'ar': 'تم التحقق'},
    'verificationFailed': {'en': 'Verification Failed', 'ar': 'فشل التحقق'},
    'invalidNationalId': {
      'en': 'Invalid information. Please enter a valid national ID and birth date in YYYY-MM-DD format',
      'ar': 'بيانات غير صحيحة. يرجى إدخال رقم هوية وطنية وتاريخ ميلاد صحيحين بصيغة YYYY-MM-DD',
    },

    // Notes & attachments screen
    'sampling': {'en': 'Sampling', 'ar': 'أخذ عينات'},

    // Review & submit screen
    'review': {'en': 'Review', 'ar': 'المراجعة'},
    'compliancePercentage': {'en': 'Compliance percentage', 'ar': 'نسبة الامتثال'},
    'nonCompliantClause': {'en': 'Non- Compliant clause', 'ar': 'بند غير ممتثل'},
    'nonCompliantItems': {'en': 'Non - Compliant Items', 'ar': 'البنود غير الممتثلة'},
    'notesAndAttachments': {'en': 'Notes and attachments', 'ar': 'الملاحظات والمرفقات'},
    'notice': {'en': 'Notice', 'ar': 'تنبيه'},
    'confirmBeforeSending': {
      'en': 'Are you sure about the data before sending?',
      'ar': 'هل أنت متأكد من البيانات قبل الإرسال؟',
    },
    'backOff': {'en': 'Back off', 'ar': 'تراجع'},
    'submit': {'en': 'Submit', 'ar': 'إرسال'},

    // Success screen
    'visitSentSuccessfully': {
      'en': 'Visit sent successfully. You can revisit it at any time from previous visits.',
      'ar': 'تم إرسال الزيارة بنجاح. يمكنك الرجوع إليها في أي وقت من الزيارات السابقة.',
    },
    'visitNumber': {'en': 'Visit number', 'ar': 'رقم الزيارة'},
  };

  String t(String key) => _values[key]?[locale.languageCode] ?? key;
}

class AppStringsDelegate extends LocalizationsDelegate<AppStrings> {
  const AppStringsDelegate();

  @override
  bool isSupported(Locale locale) => ['en', 'ar'].contains(locale.languageCode);

  @override
  Future<AppStrings> load(Locale locale) async => AppStrings(locale);

  @override
  bool shouldReload(LocalizationsDelegate<AppStrings> old) => false;
}
