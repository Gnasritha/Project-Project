package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dao.EstablishmentRepository;
import com.aaseya.momthathel.dao.InspectionCaseRepository;
import com.aaseya.momthathel.dao.IsicActivityRepository;
import com.aaseya.momthathel.dao.LicenseRepository;
import com.aaseya.momthathel.dao.UserRepository;
import com.aaseya.momthathel.dto.FacilityCreateRequest;
import com.aaseya.momthathel.dto.FacilityResponse;
import com.aaseya.momthathel.dto.InspectionTypeResponse;
import com.aaseya.momthathel.dto.LicenseLookupDto;
import com.aaseya.momthathel.dto.LicenseVerifyRequest;
import com.aaseya.momthathel.dto.LicenseVerifyResponse;
import com.aaseya.momthathel.dto.NoLicenseVisitRequest;
import com.aaseya.momthathel.dto.VisitCreateRequest;
import com.aaseya.momthathel.dto.VisitResponse;
import com.aaseya.momthathel.model.Establishment;
import com.aaseya.momthathel.model.InspectionCase;
import com.aaseya.momthathel.model.InspectionType;
import com.aaseya.momthathel.model.IsicActivity;
import com.aaseya.momthathel.model.License;
import com.aaseya.momthathel.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InspectorVisitService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private InspectionCaseRepository caseRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private IsicActivityRepository isicActivityRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<InspectionTypeResponse> getMappedInspectionTypes(Long inspectorId) {
        try {
            if (inspectorId == null) {
                throw new RuntimeException("inspectorId is required");
            }
            if (!userRepository.existsById(inspectorId)) {
                throw new RuntimeException("Inspector not found: " + inspectorId);
            }
            return userRepository.findAuthorisedActiveInspectionTypes(inspectorId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public LicenseVerifyResponse verifyLicense(LicenseVerifyRequest request) {

        LicenseVerifyResponse response = new LicenseVerifyResponse();

        try {
            if (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank()) {
                throw new RuntimeException("licenseNumber is required");
            }

            LicenseLookupDto dto = licenseRepository.findLicenseSummaryByNumber(request.getLicenseNumber());

            if (dto != null) {
                response.setFound(true);
                response.setLicenseId(dto.getLicenseId());
                response.setLicenseNumber(dto.getLicenseNumber());
                response.setLicenseStatus(dto.getLicenseStatus());
                response.setLicenseType(dto.getLicenseType());
                response.setIssueDate(dto.getIssueDate());
                response.setExpiryDate(dto.getExpiryDate());
                response.setEstablishmentId(dto.getEstablishmentId());
                response.setEstablishmentName(dto.getEstablishmentName());
                response.setActivityType(dto.getActivityType());
                response.setAddress(dto.getAddress());
                response.setCity(dto.getCity());
                response.setSuccess(true);
                response.setMessage("License found");
            } else {
                response.setFound(false);
                response.setLicenseNumber(request.getLicenseNumber());
                response.setSuccess(false);
                response.setMessage("License number not found in the registry. Please check and try again.");
            }

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public VisitResponse createVisit(VisitCreateRequest request, Long inspectorId) {

        try {
            if (request.getInspectionNumber() == null || request.getInspectionNumber().isBlank()) {
                throw new RuntimeException("inspectionNumber is required");
            }
            if (request.getInspectionTypeId() == null) {
                throw new RuntimeException("inspectionTypeId is required");
            }
            if (request.getEstablishmentId() == null) {
                throw new RuntimeException("establishmentId is required");
            }
            if (!userRepository.existsById(inspectorId)) {
                throw new RuntimeException("Inspector not found: " + inspectorId);
            }
            if (userRepository.countAuthorisedType(inspectorId, request.getInspectionTypeId()) == 0) {
                throw new RuntimeException("You are not authorised to conduct inspection type ID: "
                        + request.getInspectionTypeId());
            }
            if (caseRepository.findByInspectionNumber(request.getInspectionNumber()) != null) {
                throw new RuntimeException("Inspection number already exists: " + request.getInspectionNumber());
            }

            User inspector = entityManager.getReference(User.class, inspectorId);
            InspectionType inspectionType = entityManager.getReference(InspectionType.class, request.getInspectionTypeId());
            Establishment establishment = entityManager.getReference(Establishment.class, request.getEstablishmentId());
            License license = (request.isLicenseAvailable() && request.getLicenseId() != null)
                    ? entityManager.getReference(License.class, request.getLicenseId())
                    : null;

            InspectionCase ic = new InspectionCase();
            ic.setInspectionNumber(request.getInspectionNumber());
            ic.setInspectionType(inspectionType);
            ic.setInspector(inspector);
            ic.setEstablishment(establishment);
            ic.setLicense(license);
            ic.setInspectionMode(request.getInspectionMode());
            ic.setInspectionDate(request.getInspectionDate() != null ? request.getInspectionDate() : LocalDateTime.now());
            ic.setStatus("IN_PROGRESS");
            ic.setCurrentStage("FIELD_INSPECTION");
            ic.setLatitude(request.getLatitude());
            ic.setLongitude(request.getLongitude());

            InspectionCase saved = caseRepository.save(ic);
            caseRepository.flush();

            InspectionCase fetched = caseRepository.findByIdWithDetails(saved.getInspectionId());
            if (fetched == null) {
                throw new RuntimeException("Failed to reload saved inspection case.");
            }

            return toVisitResponse(fetched);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public FacilityResponse createFacility(FacilityCreateRequest request) {

        FacilityResponse response = new FacilityResponse();

        try {
            if (request.getFacilityName() == null || request.getFacilityName().isBlank()) {
                throw new RuntimeException("facilityName is required");
            }
            if (request.getLocation() == null || request.getLocation().isBlank()) {
                throw new RuntimeException("location is required");
            }
            if (request.getLatitude() == null || request.getLongitude() == null) {
                throw new RuntimeException("latitude and longitude are required");
            }
            if (request.getIsicId() == null) {
                throw new RuntimeException("isicId is required");
            }
            if (request.getIsicDetailId() == null) {
                throw new RuntimeException("isicDetailId is required");
            }

            IsicActivity isic = isicActivityRepository.findById(request.getIsicId());
            if (isic == null) {
                throw new RuntimeException("ISIC category not found: " + request.getIsicId());
            }
            IsicActivity detail = isicActivityRepository.findById(request.getIsicDetailId());
            if (detail == null) {
                throw new RuntimeException("ISIC detail activity not found: " + request.getIsicDetailId());
            }
            if (detail.getParent() == null || !detail.getParent().getIsicId().equals(request.getIsicId())) {
                throw new RuntimeException(
                        "ISIC detail activity " + request.getIsicDetailId()
                        + " is not a child of ISIC category " + request.getIsicId());
            }

            Establishment establishment = new Establishment();
            establishment.setEstablishmentName(request.getFacilityName());
            establishment.setAddress(request.getLocation());
            establishment.setCity(request.getCity());
            establishment.setLatitude(request.getLatitude());
            establishment.setLongitude(request.getLongitude());
            establishment.setActivityType(detail.getNameEn());
            establishment.setIsicActivity(isic);
            establishment.setIsicDetail(detail);
            establishment.setLicenseAvailable(Boolean.FALSE);

            Establishment saved = establishmentRepository.save(establishment);

            response.setEstablishmentId(saved.getEstablishmentId());
            response.setFacilityName(saved.getEstablishmentName());
            response.setLocation(saved.getAddress());
            response.setCity(saved.getCity());
            response.setLatitude(saved.getLatitude());
            response.setLongitude(saved.getLongitude());
            response.setIsicName(saved.getIsicActivity() != null ? saved.getIsicActivity().getNameEn() : null);
            response.setDetailActivityName(saved.getIsicDetail() != null ? saved.getIsicDetail().getNameEn() : null);
            response.setLicenseAvailable(Boolean.TRUE.equals(saved.getLicenseAvailable()));
            response.setSuccess(true);
            response.setMessage("Facility created successfully");

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public VisitResponse createNoLicenseVisit(NoLicenseVisitRequest request, Long inspectorId) {
        try {
            if (request.getFacility() == null) {
                throw new RuntimeException("facility is required");
            }

            FacilityResponse facility = createFacility(request.getFacility());

            VisitCreateRequest visit = new VisitCreateRequest();
            visit.setInspectionNumber(request.getInspectionNumber());
            visit.setInspectionTypeId(request.getInspectionTypeId());
            visit.setEstablishmentId(facility.getEstablishmentId());
            visit.setLicenseId(null);
            visit.setLicenseAvailable(false);
            visit.setLicenseEntryMethod("MAP_SELECTION");
            visit.setInspectionMode(request.getInspectionMode());
            visit.setInspectionDate(LocalDateTime.now());
            visit.setLatitude(request.getFacility().getLatitude());
            visit.setLongitude(request.getFacility().getLongitude());
            visit.setFamilyRelationshipDisclosed(false);

            return createVisit(visit, inspectorId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private VisitResponse toVisitResponse(InspectionCase ic) {

        VisitResponse r = new VisitResponse();
        r.setInspectionId(ic.getInspectionId());
        r.setInspectionNumber(ic.getInspectionNumber());
        r.setInspectionType(ic.getInspectionType() != null ? ic.getInspectionType().getInspectionName() : null);
        r.setInspectionMode(ic.getInspectionMode());
        r.setStatus(ic.getStatus());
        r.setCurrentStage(ic.getCurrentStage());
        r.setInspectionDate(ic.getInspectionDate());
        r.setCreatedAt(ic.getCreatedAt());

        License lic = ic.getLicense();
        if (lic != null) {
            r.setLicenseNumber(lic.getLicenseNumber());
            r.setLicenseStatus(lic.getLicenseStatus());
            r.setLicenseType(lic.getLicenseType());
        }

        Establishment e = ic.getEstablishment();
        if (e != null) {
            r.setEstablishmentId(e.getEstablishmentId());
            r.setEstablishmentName(e.getEstablishmentName());
            r.setActivityType(e.getActivityType());
            r.setCity(e.getCity());
        }

        r.setSuccess(true);
        r.setMessage("Visit created successfully");
        return r;
    }
}
