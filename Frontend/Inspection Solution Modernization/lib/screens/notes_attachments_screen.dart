import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import '../core/localization/app_strings.dart';
import '../routes/app_routes.dart';
import '../services/session_state.dart';
import '../theme/app_theme.dart';
import '../widgets/common_buttons.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/image_upload_box.dart';
import '../widgets/screen_scaffold.dart';

/// Step: Notes & Attachments. Sampling flag, free-text inspector notes, and
/// image uploads. Reference: Sprint2 screenshots 13 & 14.
class NotesAttachmentsScreen extends StatefulWidget {
  const NotesAttachmentsScreen({super.key});

  @override
  State<NotesAttachmentsScreen> createState() => _NotesAttachmentsScreenState();
}

class _NotesAttachmentsScreenState extends State<NotesAttachmentsScreen> {
  bool _sampling = false;
  final _notes = TextEditingController();
  final List<PickedImage> _images = [];
  bool _picking = false;

  @override
  void dispose() {
    _notes.dispose();
    super.dispose();
  }

  Future<void> _pickImage() async {
    setState(() => _picking = true);
    try {
      final picked =
          await ImagePicker().pickImage(source: ImageSource.gallery);
      if (picked == null) return;
      final bytes = kIsWeb ? await picked.readAsBytes() : null;
      if (!mounted) return;
      setState(() {
        _images.add(PickedImage(
          name: picked.name,
          path: kIsWeb ? null : picked.path,
          bytes: bytes,
        ));
      });
    } catch (_) {
      // picker unavailable / cancelled — ignore
    } finally {
      if (mounted) setState(() => _picking = false);
    }
  }

  void _saveDraftToSession() {
    // Persist to the in-memory session so the Review screen can read it back.
    // (Wire to StorageService for true offline drafts in a later pass.)
    final session = context.read<SessionState>();
    session.setNotesDraft(
      sampling: _sampling,
      notes: _notes.text.trim(),
      attachmentCount: _images.length,
    );
  }

  @override
  Widget build(BuildContext context) {
    final s = AppStrings.of(context);
    final session = context.watch<SessionState>();

    return InspectionScreenScaffold(
      title: s.t('notesAndAttachments'),
      insNumber: session.caseDisplayId,
      totalSteps: 7,
      currentStep: 6,
      footer: FooterNavBar(
        previousLabel: s.t('previous'),
        nextLabel: s.t('next'),
        onPrevious: () => Navigator.of(context).maybePop(),
        onNext: () {
          _saveDraftToSession();
          Navigator.of(context).pushNamed(Routes.reviewSubmit);
        },
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Sampling checkbox card
          GestureDetector(
            onTap: () => setState(() => _sampling = !_sampling),
            behavior: HitTestBehavior.opaque,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
              decoration: BoxDecoration(
                color: Colors.white,
                border: Border.all(color: AppTheme.border),
                borderRadius: BorderRadius.circular(AppTheme.cardRadius),
              ),
              child: Row(
                children: [
                  SizedBox(
                    width: 24,
                    height: 24,
                    child: Checkbox(
                      value: _sampling,
                      onChanged: (v) => setState(() => _sampling = v ?? false),
                    ),
                  ),
                  const SizedBox(width: 10),
                  Text(
                    s.t('sampling'),
                    style: const TextStyle(
                      color: AppTheme.textPrimary,
                      fontWeight: FontWeight.w600,
                      fontSize: 15,
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 18),

          // Inspector notes
          Text(
            s.t('inspectorNotes'),
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 8),
          CustomTextField(
            hint: s.t('notes'),
            controller: _notes,
            maxLines: 4,
          ),
          const SizedBox(height: 18),

          // Images & attachments
          Text(
            s.t('imagesAndAttachments'),
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 8),
          ImageUploadBox(onTap: _pickImage, busy: _picking),
          if (_images.isNotEmpty) ...[
            const SizedBox(height: 12),
            ImageThumbnailStrip(
              images: _images,
              onRemove: (i) => setState(() => _images.removeAt(i)),
            ),
          ],
        ],
      ),
    );
  }
}
