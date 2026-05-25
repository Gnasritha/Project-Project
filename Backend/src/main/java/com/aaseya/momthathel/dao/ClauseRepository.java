package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.model.Clause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClauseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Clause findById(Long clauseId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Clause> cq = cb.createQuery(Clause.class);
        Root<Clause> root = cq.from(Clause.class);
        cq.select(root).where(cb.equal(root.get("clauseId"), clauseId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public List<Clause> findAllOrderByClauseCode() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Clause> cq = cb.createQuery(Clause.class);
        Root<Clause> root = cq.from(Clause.class);
        cq.select(root).orderBy(cb.asc(root.get("clauseCode")));
        return entityManager.createQuery(cq).getResultList();
    }
}
