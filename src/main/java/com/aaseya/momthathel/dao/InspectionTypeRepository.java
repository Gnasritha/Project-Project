package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.model.InspectionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InspectionTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public InspectionType findById(Long inspectionTypeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionType> cq = cb.createQuery(InspectionType.class);
        Root<InspectionType> root = cq.from(InspectionType.class);
        cq.select(root).where(cb.equal(root.get("inspectionTypeId"), inspectionTypeId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public List<InspectionType> findByActiveTrue() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionType> cq = cb.createQuery(InspectionType.class);
        Root<InspectionType> root = cq.from(InspectionType.class);
        cq.select(root).where(cb.isTrue(root.get("active")));
        return entityManager.createQuery(cq).getResultList();
    }

    public InspectionType findByInspectionCode(String inspectionCode) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionType> cq = cb.createQuery(InspectionType.class);
        Root<InspectionType> root = cq.from(InspectionType.class);
        cq.select(root).where(cb.equal(root.get("inspectionCode"), inspectionCode));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }
}
