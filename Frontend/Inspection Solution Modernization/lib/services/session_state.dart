import 'package:flutter/foundation.dart';
import '../config/app_constants.dart';
import '../data/models/auth_models.dart';
import '../data/models/visit_dtos.dart';

/// In-memory session shared across the inspection flow.
///
/// Holds the current user, inspector id, the in-progress visit context
/// (inspectionId, license, facility data) so screens can hand off without
/// passing data through Navigator arguments.
class SessionState extends ChangeNotifier {
  CurrentUser? _user;
  int _inspectorId = AppConstants.defaultInspectorId;
  int? _inspectionId;
  String? _inspectionNumber;
  String? _businessKey;
  int? _processInstanceKey;
  String? _licenseNumber;
  LicenseVerificationResponse? _licenseInfo;
  InspectionType? _selectedType;

  // ── No-license flow — location picked from the map ("lat, lng") ─────────
  String? _pickedLocation;

  // ── Sprint 2 — Notes & Attachments step draft ──────────────────────────
  bool _samplingDone = false;
  String _inspectorNotes = '';
  int _attachmentCount = 0;

  CurrentUser? get user => _user;
  int get inspectorId => _inspectorId;
  int? get inspectionId => _inspectionId;
  String? get inspectionNumber => _inspectionNumber;
  String? get businessKey => _businessKey;
  int? get processInstanceKey => _processInstanceKey;

  /// Case ID shown under the screen title — the backend business key when
  /// available (e.g. INS-20260521-00042), otherwise the inspection number.
  String? get caseDisplayId => _businessKey ?? _inspectionNumber;

  String? get licenseNumber => _licenseNumber;
  LicenseVerificationResponse? get licenseInfo => _licenseInfo;
  InspectionType? get selectedType => _selectedType;
  bool get samplingDone => _samplingDone;
  String get inspectorNotes => _inspectorNotes;
  int get attachmentCount => _attachmentCount;
  String? get pickedLocation => _pickedLocation;

  /// Stores the map-picked location for the no-license flow so the
  /// Facility Status screen can pre-fill its Location field.
  void setPickedLocation(String? location) {
    _pickedLocation = location;
    notifyListeners();
  }

  /// Stores the Notes & Attachments step so the Review & Submit screen can
  /// read it back without Navigator argument passing.
  void setNotesDraft({
    required bool sampling,
    required String notes,
    required int attachmentCount,
  }) {
    _samplingDone = sampling;
    _inspectorNotes = notes;
    _attachmentCount = attachmentCount;
    notifyListeners();
  }

  void setUser(CurrentUser? user) {
    _user = user;
    if (user != null) _inspectorId = user.id;
    notifyListeners();
  }

  void setSelectedType(InspectionType? type) {
    _selectedType = type;
    notifyListeners();
  }

  void setLicense({
    required String licenseNumber,
    LicenseVerificationResponse? info,
  }) {
    _licenseNumber = licenseNumber;
    _licenseInfo = info;
    notifyListeners();
  }

  void setVisit({
    required int inspectionId,
    String? inspectionNumber,
    String? businessKey,
    int? processInstanceKey,
  }) {
    _inspectionId = inspectionId;
    _inspectionNumber = inspectionNumber;
    _businessKey = businessKey;
    _processInstanceKey = processInstanceKey;
    notifyListeners();
  }

  void resetVisit() {
    _inspectionId = null;
    _inspectionNumber = null;
    _businessKey = null;
    _processInstanceKey = null;
    _licenseNumber = null;
    _licenseInfo = null;
    _selectedType = null;
    _samplingDone = false;
    _inspectorNotes = '';
    _attachmentCount = 0;
    _pickedLocation = null;
    notifyListeners();
  }

  void clear() {
    _user = null;
    _inspectorId = AppConstants.defaultInspectorId;
    resetVisit();
  }
}
