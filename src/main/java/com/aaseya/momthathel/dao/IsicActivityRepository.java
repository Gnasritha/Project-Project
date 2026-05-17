package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.model.IsicActivity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IsicActivityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public IsicActivity findById(Long isicId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IsicActivity> cq = cb.createQuery(IsicActivity.class);
        Root<IsicActivity> root = cq.from(IsicActivity.class);
        cq.select(root).where(cb.equal(root.get("isicId"), isicId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public List<IsicActivity> findTopLevelActive() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IsicActivity> cq = cb.createQuery(IsicActivity.class);
        Root<IsicActivity> root = cq.from(IsicActivity.class);

        cq.select(root)
          .where(cb.and(
                  cb.isNull(root.get("parent")),
                  cb.isTrue(root.get("active"))))
          .orderBy(cb.asc(root.get("nameEn")));

        return entityManager.createQuery(cq).getResultList();
    }

    public List<IsicActivity> findChildrenActive(Long parentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IsicActivity> cq = cb.createQuery(IsicActivity.class);
        Root<IsicActivity> root = cq.from(IsicActivity.class);

        cq.select(root)
          .where(cb.and(
                  cb.equal(root.get("parent").get("isicId"), parentId),
                  cb.isTrue(root.get("active"))))
          .orderBy(cb.asc(root.get("nameEn")));

        return entityManager.createQuery(cq).getResultList();
    }
}
