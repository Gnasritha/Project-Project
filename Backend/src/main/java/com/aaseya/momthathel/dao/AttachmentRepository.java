package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.model.Attachment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AttachmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Attachment findById(Long attachmentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Attachment> cq = cb.createQuery(Attachment.class);
        Root<Attachment> root = cq.from(Attachment.class);
        cq.select(root).where(cb.equal(root.get("attachmentId"), attachmentId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Attachment findByIdAndInspectionId(Long attachmentId, Long inspectionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Attachment> cq = cb.createQuery(Attachment.class);
        Root<Attachment> root = cq.from(Attachment.class);

        cq.select(root).where(cb.and(
                cb.equal(root.get("attachmentId"), attachmentId),
                cb.equal(root.get("inspectionCase").get("inspectionId"), inspectionId)));

        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public List<Attachment> findByInspectionId(Long inspectionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Attachment> cq = cb.createQuery(Attachment.class);
        Root<Attachment> root = cq.from(Attachment.class);

        cq.select(root)
          .where(cb.equal(root.get("inspectionCase").get("inspectionId"), inspectionId))
          .orderBy(cb.asc(root.get("uploadedAt")));

        return entityManager.createQuery(cq).getResultList();
    }

    public long countByInspectionId(Long inspectionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Attachment> root = cq.from(Attachment.class);

        cq.select(cb.count(root))
          .where(cb.equal(root.get("inspectionCase").get("inspectionId"), inspectionId));

        Long count = entityManager.createQuery(cq).getSingleResult();
        return count != null ? count : 0L;
    }

    public Attachment save(Attachment attachment) {
        if (attachment.getAttachmentId() == null) {
            entityManager.persist(attachment);
            return attachment;
        } else {
            return entityManager.merge(attachment);
        }
    }

    public void delete(Attachment attachment) {
        Attachment managed = entityManager.contains(attachment)
                ? attachment
                : entityManager.merge(attachment);
        entityManager.remove(managed);
    }
}
