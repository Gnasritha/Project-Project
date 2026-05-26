package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dto.ViolatorVerifyEntityRequest;
import com.aaseya.momthathel.dto.ViolatorVerifyIndividualRequest;
import com.aaseya.momthathel.dto.ViolatorVerifyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Verifies the identity of a violator entered through the mobile inspection
 * flow. Two paths:
 *
 * <ul>
 *   <li><strong>Individual</strong> — Saudi National ID / Iqama + Gregorian
 *       birth date. A production integration would hit Yakeen / Absher;
 *       this stub does format validation and returns a deterministic mock
 *       profile so the UI flow is exercisable end-to-end.</li>
 *   <li><strong>Entity</strong> — national facility number. Production:
 *       Wathiq / MCI commercial registry. Stub: format check only.</li>
 * </ul>
 *
 * <p>The Arabic / English error message returned on a failed verify is
 * what the bottom-sheet's red banner renders (see Sprint 2 screenshot 4).</p>
 */
@Service
public class ViolatorVerificationService {

    private static final Logger log = LoggerFactory.getLogger(ViolatorVerificationService.class);

    /** Accept both common written-by-hand formats: 2026-05-22 and 2026/5/22. */
    private static final List<DateTimeFormatter> BIRTH_DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    /** Identical to the Arabic string the mobile app shows on the red banner. */
    private static final String INVALID_INDIVIDUAL_MSG =
            "Invalid information. Please enter a valid national ID and a birth date in YYYY-MM-DD format.";

    private static final String INVALID_ENTITY_MSG =
            "Facility number not found. Please enter a valid national facility number.";

    public ViolatorVerifyResponse verifyIndividual(ViolatorVerifyIndividualRequest req) {
        String id = req == null ? null : safeTrim(req.getIdNumber());
        String birth = req == null ? null : safeTrim(req.getBirthDate());

        if (id == null || !id.matches("\\d{10}")) {
            log.info("[Violator] Individual verify FAILED — bad ID format: '{}'", id);
            return ViolatorVerifyResponse.failed(INVALID_INDIVIDUAL_MSG);
        }
        LocalDate dob = parseBirthDate(birth);
        if (dob == null || dob.isAfter(LocalDate.now()) || dob.getYear() < 1900) {
            log.info("[Violator] Individual verify FAILED — bad birth date: '{}'", birth);
            return ViolatorVerifyResponse.failed(INVALID_INDIVIDUAL_MSG);
        }

        // Deterministic stub profile so the UI shows a stable, plausible record.
        String name = mockIndividualName(id);
        log.info("[Violator] Individual VERIFIED — id={}, name={}", id, name);
        return ViolatorVerifyResponse.verifiedIndividual(name, id, null);
    }

    public ViolatorVerifyResponse verifyEntity(ViolatorVerifyEntityRequest req) {
        String facility = req == null ? null : safeTrim(req.getNationalFacilityNumber());
        if (facility == null || !facility.matches("\\d{7,15}")) {
            log.info("[Violator] Entity verify FAILED — bad facility number: '{}'", facility);
            return ViolatorVerifyResponse.failed(INVALID_ENTITY_MSG);
        }
        String name = mockEntityName(facility);
        log.info("[Violator] Entity VERIFIED — facility={}, name={}", facility, name);
        return ViolatorVerifyResponse.verifiedEntity(name, facility, null);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private static String safeTrim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static LocalDate parseBirthDate(String raw) {
        if (raw == null) return null;
        // Strip any "1990/5/22 - 1410/10/27" Hijri appendage the picker may emit.
        String g = raw.contains(" - ") ? raw.substring(0, raw.indexOf(" - ")).trim() : raw;
        for (DateTimeFormatter f : BIRTH_DATE_FORMATS) {
            try {
                return LocalDate.parse(g, f);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }
        return null;
    }

    /**
     * Stub Arabic name keyed on the last digit of the ID so the UI shows
     * variety while staying deterministic.
     */
    private static String mockIndividualName(String id) {
        String[] names = {
                "زيد نزار عزمي الزبن",
                "محمد عبدالله الشهري",
                "سعيد فايز القحطاني",
                "خالد عبدالعزيز الحربي",
                "فيصل سلطان الدوسري",
                "أحمد سعد الغامدي",
                "ناصر عبدالرحمن العتيبي",
                "سلمان عبدالله المطيري",
                "تركي حسن الزهراني",
                "بدر فهد العنزي"
        };
        int idx = Character.digit(id.charAt(id.length() - 1), 10);
        return names[Math.max(0, idx)];
    }

    private static String mockEntityName(String facility) {
        String[] names = {
                "شركة جمرة عرب للتعدين شركة شخص واحد",
                "مؤسسة الواحة للمقاولات",
                "شركة الأفق التجارية المحدودة",
                "مصنع النور للصناعات الغذائية",
                "شركة الفجر الجديد للخدمات",
                "مؤسسة الرياض للنقل",
                "شركة البحر الأبيض للاستيراد",
                "مصنع الذهبي للأثاث",
                "شركة المملكة الخضراء للزراعة",
                "مؤسسة الشمس للتطوير العقاري"
        };
        int idx = Character.digit(facility.charAt(facility.length() - 1), 10);
        return names[Math.max(0, idx)];
    }
}
