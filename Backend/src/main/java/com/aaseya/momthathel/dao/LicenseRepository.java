package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.dto.LicenseLookupDto;
import com.aaseya.momthathel.model.License;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class LicenseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public License findById(Long licenseId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<License> cq = cb.createQuery(License.class);
        Root<License> root = cq.from(License.class);
        cq.select(root).where(cb.equal(root.get("licenseId"), licenseId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public LicenseLookupDto findLicenseSummaryByNumber(String licenseNumber) {
        String jpql = """
                SELECT new com.aaseya.momthathel.dto.LicenseLookupDto(
                    l.licenseId,
                    l.licenseNumber,
                    l.licenseStatus,
                    l.licenseType,
                    l.issueDate,
                    l.expiryDate,
                    e.establishmentId,
                    e.establishmentName,
                    e.activityType,
                    e.address,
                    e.city,
                    e.mobileNumber
                )
                FROM License l
                JOIN l.establishment e
                WHERE l.licenseNumber = :licenseNumber
                """;
        return entityManager.createQuery(jpql, LicenseLookupDto.class)
                .setParameter("licenseNumber", licenseNumber)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
