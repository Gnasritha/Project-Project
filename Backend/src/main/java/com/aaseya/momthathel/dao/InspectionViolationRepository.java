package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.dto.PreviousViolationDto;
import com.aaseya.momthathel.model.InspectionViolation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InspectionViolationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public InspectionViolation findById(Long violationId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionViolation> cq = cb.createQuery(InspectionViolation.class);
        Root<InspectionViolation> root = cq.from(InspectionViolation.class);
        cq.select(root).where(cb.equal(root.get("violationId"), violationId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public List<InspectionViolation> findByInspectionId(Long inspectionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionViolation> cq = cb.createQuery(InspectionViolation.class);
        Root<InspectionViolation> root = cq.from(InspectionViolation.class);

        cq.select(root)
          .where(cb.equal(root.get("inspectionCase").get("inspectionId"), inspectionId))
          .orderBy(cb.asc(root.get("violationId")));

        return entityManager.createQuery(cq).getResultList();
    }

    /**
     * ISM-7: historic violations linked to the establishment behind a license.
     * Optionally excludes a specific inspection (the in-progress one).
     */
    public List<PreviousViolationDto> findPreviousViolationsByLicense(String licenseNumber, Long excludeInspectionId) {
        String jpql = """
                SELECT new com.aaseya.momthathel.dto.PreviousViolationDto(
                    v.violationId,
                    ic.inspectionId,
                    ic.inspectionNumber,
                    ic.inspectionDate,
                    c.clauseId,
                    c.clauseCode,
                    c.clauseName,
                    v.severity,
                    v.violationDescription,
                    v.correctiveAction
                )
                FROM InspectionViolation v
                JOIN v.inspectionCase ic
                JOIN ic.license l
                JOIN v.clause c
                WHERE l.licenseNumber = :licenseNumber
                  AND (:excludeInspectionId IS NULL OR ic.inspectionId <> :excludeInspectionId)
                ORDER BY ic.inspectionDate DESC, v.violationId ASC
                """;
        return entityManager.createQuery(jpql, PreviousViolationDto.class)
                .setParameter("licenseNumber", licenseNumber)
                .setParameter("excludeInspectionId", excludeInspectionId)
                .getResultList();
    }

    public InspectionViolation save(InspectionViolation violation) {
        if (violation.getViolationId() == null) {
            entityManager.persist(violation);
            return violation;
        } else {
            return entityManager.merge(violation);
        }
    }

    public void delete(InspectionViolation violation) {
        InspectionViolation managed = entityManager.contains(violation)
                ? violation
                : entityManager.merge(violation);
        entityManager.remove(managed);
    }
}
