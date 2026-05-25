package com.aaseya.momthathel.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps to the {@code ISIC_ACTIVITY} table.
 * Self-referencing hierarchy: rows with {@code parent = null} are top-level
 * ISIC categories; rows with a non-null parent are the detail activities.
 */
@Entity
@Table(name = "isic_activity",
       uniqueConstraints = @UniqueConstraint(name = "uk_isic_code", columnNames = "isic_code"))
public class IsicActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "isic_activity_seq")
    @SequenceGenerator(name = "isic_activity_seq", sequenceName = "isic_activity_isic_id_seq", allocationSize = 1)
    @Column(name = "isic_id")
    private Long isicId;

    @Column(name = "isic_code", nullable = false, unique = true, length = 50)
    private String isicCode;

    @Column(name = "name_en", nullable = false, length = 300)
    private String nameEn;

    @Column(name = "name_ar", length = 300)
    private String nameAr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_isic_parent"))
    private IsicActivity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<IsicActivity> children = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    public IsicActivity() {}

    public Long getIsicId() { return isicId; }
    public void setIsicId(Long isicId) { this.isicId = isicId; }

    public String getIsicCode() { return isicCode; }
    public void setIsicCode(String isicCode) { this.isicCode = isicCode; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public IsicActivity getParent() { return parent; }
    public void setParent(IsicActivity parent) { this.parent = parent; }

    public List<IsicActivity> getChildren() { return children; }
    public void setChildren(List<IsicActivity> children) { this.children = children; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
