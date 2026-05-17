package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.model.Establishment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class EstablishmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Establishment findById(Long establishmentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Establishment> cq = cb.createQuery(Establishment.class);
        Root<Establishment> root = cq.from(Establishment.class);
        cq.select(root).where(cb.equal(root.get("establishmentId"), establishmentId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Establishment save(Establishment establishment) {
        if (establishment.getEstablishmentId() == null) {
            entityManager.persist(establishment);
            return establishment;
        } else {
            return entityManager.merge(establishment);
        }
    }
}
