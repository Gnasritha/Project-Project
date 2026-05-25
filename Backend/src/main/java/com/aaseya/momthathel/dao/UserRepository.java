package com.aaseya.momthathel.dao;

import com.aaseya.momthathel.dto.InspectionTypeResponse;
import com.aaseya.momthathel.model.InspectionType;
import com.aaseya.momthathel.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User findById(Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root).where(cb.equal(root.get("userId"), userId));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public User findByUsername(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root).where(cb.equal(root.get("username"), username));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public User findByMobileNumber(String mobileNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root).where(cb.equal(root.get("mobileNumber"), mobileNumber));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public User findByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root).where(cb.equal(root.get("email"), email));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public User findByAuthToken(String authToken) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root).where(cb.equal(root.get("authToken"), authToken));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public boolean existsById(Long userId) {
        return findById(userId) != null;
    }

    public void saveOrUpdate(User user) {
        if (user.getUserId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    /**
     * Finds users whose password_hash starts with the given placeholder prefix
     * (e.g. {@code $2a$10$dummy}). Used by {@code AuthInitializer} to seed
     * real bcrypt hashes for users inserted via {@code seed-data.sql}.
     */
    public List<User> findAllWithDummyPassword(String placeholderPrefix) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.passwordHash LIKE :prefix", User.class)
                .setParameter("prefix", placeholderPrefix + "%")
                .getResultList();
    }

    public List<InspectionTypeResponse> findAuthorisedActiveInspectionTypes(Long userId) {
        // Inspection types are catalog-wide — every authenticated inspector sees
        // every active type. The user_inspection_type table is no longer consulted
        // for visibility; it is retained for potential future per-user restrictions.
        String jpql = """
                SELECT new com.aaseya.momthathel.dto.InspectionTypeResponse(
                    it.inspectionTypeId,
                    it.inspectionCode,
                    it.inspectionName,
                    it.active
                )
                FROM InspectionType it
                WHERE it.active = true
                ORDER BY it.inspectionName ASC
                """;
        return entityManager.createQuery(jpql, InspectionTypeResponse.class)
                .getResultList();
    }

    public long countAuthorisedType(Long userId, Long typeId) {
        // Any logged-in inspector can conduct any active inspection type.
        // Authorisation here means "the type exists and is active".
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<InspectionType> root = cq.from(InspectionType.class);

        cq.select(cb.count(root))
          .where(cb.and(
                  cb.equal(root.get("inspectionTypeId"), typeId),
                  cb.isTrue(root.get("active"))));

        Long count = entityManager.createQuery(cq).getSingleResult();
        return count != null ? count : 0L;
    }
}
