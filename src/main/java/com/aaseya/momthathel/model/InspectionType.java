package com.aaseya.momthathel.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps to the {@code INSPECTION_TYPE} table.
 */
@Entity
@Table(name = "inspection_type")
public class InspectionType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inspection_type_seq")
    @SequenceGenerator(name = "inspection_type_seq", sequenceName = "inspection_type_inspection_type_id_seq", allocationSize = 1)
    @Column(name = "inspection_type_id")
    private Long inspectionTypeId;

    @Column(name = "inspection_name", nullable = false, length = 200)
    private String inspectionName;

    @Column(name = "inspection_code", nullable = false, unique = true, length = 50)
    private String inspectionCode;

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @OneToMany(mappedBy = "inspectionType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Establishment> establishments = new ArrayList<>();

    public InspectionType() {}

    public Long getInspectionTypeId() { return inspectionTypeId; }
    public void setInspectionTypeId(Long inspectionTypeId) { this.inspectionTypeId = inspectionTypeId; }

    public String getInspectionName() { return inspectionName; }
    public void setInspectionName(String inspectionName) { this.inspectionName = inspectionName; }

    public String getInspectionCode() { return inspectionCode; }
    public void setInspectionCode(String inspectionCode) { this.inspectionCode = inspectionCode; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<Establishment> getEstablishments() { return establishments; }
    public void setEstablishments(List<Establishment> establishments) { this.establishments = establishments; }
}
