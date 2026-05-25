package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.model.InspectionCase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InspectionCaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public InspectionCase findById(Long inspectionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
        Root<InspectionCase> root = cq.from(InspectionCase.class);
        cq.select(root).where(cb.equal(root.get("inspectionId"), inspectionId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public boolean existsById(Long inspectionId) {
        return findById(inspectionId) != null;
    }

    public InspectionCase findByInspectionNumber(String inspectionNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
        Root<InspectionCase> root = cq.from(InspectionCase.class);
        cq.select(root).where(cb.equal(root.get("inspectionNumber"), inspectionNumber));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public List<InspectionCase> findByInspectorId(Long inspectorId) {
        String jpql = """
                SELECT ic FROM InspectionCase ic
                JOIN FETCH ic.inspectionType
                JOIN FETCH ic.establishment
                LEFT JOIN FETCH ic.license
                WHERE ic.inspector.userId = :inspectorId
                ORDER BY ic.createdAt DESC
                """;
        return entityManager.createQuery(jpql, InspectionCase.class)
                .setParameter("inspectorId", inspectorId)
                .getResultList();
    }

    public InspectionCase findByIdWithDetails(Long inspectionId) {
        String jpql = """
                SELECT ic FROM InspectionCase ic
                JOIN FETCH ic.inspectionType
                JOIN FETCH ic.establishment
                LEFT JOIN FETCH ic.license
                WHERE ic.inspectionId = :inspectionId
                """;
        return entityManager.createQuery(jpql, InspectionCase.class)
                .setParameter("inspectionId", inspectionId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public InspectionCase save(InspectionCase inspectionCase) {
        if (inspectionCase.getInspectionId() == null) {
            entityManager.persist(inspectionCase);
            return inspectionCase;
        } else {
            return entityManager.merge(inspectionCase);
        }
    }

    public void flush() {
        entityManager.flush();
    }
}
