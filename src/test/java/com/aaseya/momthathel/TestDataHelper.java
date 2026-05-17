package com.aaseya.momthathel;

import com.aaseya.momthathel.model.Attachment;
import com.aaseya.momthathel.model.Clause;
import com.aaseya.momthathel.model.Establishment;
import com.aaseya.momthathel.model.InspectionCase;
import com.aaseya.momthathel.model.InspectionType;
import com.aaseya.momthathel.model.InspectionViolation;
import com.aaseya.momthathel.model.IsicActivity;
import com.aaseya.momthathel.model.License;
import com.aaseya.momthathel.model.Role;
import com.aaseya.momthathel.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Spring-managed seed helper that controllers' tests use to bootstrap
 * the H2 database before each test. All inserts go through the
 * EntityManager directly to avoid touching the production repositories.
 */
@TestComponent
public class TestDataHelper {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void clearAll() {
        em.createQuery("DELETE FROM InspectionViolation").executeUpdate();
        em.createQuery("DELETE FROM Attachment").executeUpdate();
        em.createQuery("DELETE FROM InspectionCase").executeUpdate();
        em.createQuery("DELETE FROM License").executeUpdate();
        em.createQuery("DELETE FROM Establishment").executeUpdate();
        em.createQuery("DELETE FROM User u WHERE u.userId IS NOT NULL").executeUpdate();
        em.createQuery("DELETE FROM Role").executeUpdate();
        em.createQuery("DELETE FROM InspectionType").executeUpdate();
        em.createQuery("DELETE FROM Clause").executeUpdate();
        em.createQuery("DELETE FROM IsicActivity").executeUpdate();
        em.flush();
        em.clear();
    }

    @Transactional
    public Role createRole(String name) {
        Role r = new Role();
        r.setRoleName(name);
        em.persist(r);
        em.flush();
        return r;
    }

    @Transactional
    public InspectionType createInspectionType(String code, String name, boolean active) {
        InspectionType it = new InspectionType();
        it.setInspectionCode(code);
        it.setInspectionName(name);
        it.setActive(active);
        em.persist(it);
        em.flush();
        return it;
    }

    @Transactional
    public Clause createClause(String code, String name, String severity) {
        Clause c = new Clause();
        c.setClauseCode(code);
        c.setClauseName(name);
        c.setSeverityLevel(severity);
        em.persist(c);
        em.flush();
        return c;
    }

    @Transactional
    public User createInspector(Role role, String username, String fullName,
                                String employeeId, Set<InspectionType> authorisedTypes) {
        User u = new User();
        u.setRole(role);
        u.setUsername(username);
        u.setFullName(fullName);
        u.setEmployeeId(employeeId);
        u.setPasswordHash("$2a$10$dummyhash");
        u.setStatus("ACTIVE");
        u.setLanguage("en");
        u.setAuthorisedInspectionTypes(authorisedTypes != null ? authorisedTypes : new HashSet<>());
        em.persist(u);
        em.flush();
        return u;
    }

    @Transactional
    public void setUserToken(Long userId, String token, LocalDateTime expiresAt) {
        User u = em.find(User.class, userId);
        u.setAuthToken(token);
        u.setTokenGeneratedAt(LocalDateTime.now());
        u.setTokenExpiresAt(expiresAt);
        em.merge(u);
        em.flush();
    }

    @Transactional
    public Establishment createEstablishment(String name, String activity, String city,
                                             InspectionType inspectionType) {
        Establishment e = new Establishment();
        e.setEstablishmentName(name);
        e.setActivityType(activity);
        e.setCity(city);
        e.setAddress(city + " main road");
        e.setLatitude(24.71);
        e.setLongitude(46.67);
        e.setInspectionType(inspectionType);
        e.setLicenseAvailable(Boolean.TRUE);
        em.persist(e);
        em.flush();
        return e;
    }

    @Transactional
    public License createLicense(String licenseNumber, Establishment establishment, String status, String type) {
        License l = new License();
        l.setLicenseNumber(licenseNumber);
        l.setEstablishment(establishment);
        l.setLicenseStatus(status);
        l.setLicenseType(type);
        l.setIssueDate(LocalDate.now().minusYears(1));
        l.setExpiryDate(LocalDate.now().plusYears(1));
        em.persist(l);
        em.flush();
        return l;
    }

    @Transactional
    public IsicActivity createIsicCategory(String code, String nameEn, String nameAr) {
        IsicActivity i = new IsicActivity();
        i.setIsicCode(code);
        i.setNameEn(nameEn);
        i.setNameAr(nameAr);
        i.setActive(Boolean.TRUE);
        em.persist(i);
        em.flush();
        return i;
    }

    @Transactional
    public IsicActivity createIsicDetail(String code, String nameEn, String nameAr, IsicActivity parent) {
        IsicActivity i = new IsicActivity();
        i.setIsicCode(code);
        i.setNameEn(nameEn);
        i.setNameAr(nameAr);
        i.setParent(parent);
        i.setActive(Boolean.TRUE);
        em.persist(i);
        em.flush();
        return i;
    }

    @Transactional
    public InspectionCase createInspectionCase(String number, User inspector, InspectionType type,
                                               Establishment establishment, License license) {
        InspectionCase ic = new InspectionCase();
        ic.setInspectionNumber(number);
        ic.setInspector(inspector);
        ic.setInspectionType(type);
        ic.setEstablishment(establishment);
        ic.setLicense(license);
        ic.setInspectionMode("ON_SITE");
        ic.setStatus("IN_PROGRESS");
        ic.setCurrentStage("FIELD_INSPECTION");
        ic.setInspectionDate(LocalDateTime.now());
        em.persist(ic);
        em.flush();
        return ic;
    }

    @Transactional
    public InspectionViolation createViolation(InspectionCase ic, Clause clause, String severity,
                                               String description) {
        InspectionViolation v = new InspectionViolation();
        v.setInspectionCase(ic);
        v.setClause(clause);
        v.setSeverity(severity);
        v.setViolationDescription(description);
        v.setCorrectiveAction("Fix immediately");
        em.persist(v);
        em.flush();
        return v;
    }

    @Transactional
    public Attachment createAttachment(InspectionCase ic, String fileName, String filePath, String type) {
        Attachment a = new Attachment();
        a.setInspectionCase(ic);
        a.setFileName(fileName);
        a.setFilePath(filePath);
        a.setFileType(type);
        em.persist(a);
        em.flush();
        return a;
    }
}
