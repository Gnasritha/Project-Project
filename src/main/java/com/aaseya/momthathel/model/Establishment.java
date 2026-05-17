package com.aaseya.momthathel.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps to the {@code ESTABLISHMENT} table.
 */
@Entity
@Table(name = "establishment")
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "establishment_seq")
    @SequenceGenerator(name = "establishment_seq", sequenceName = "establishment_establishment_id_seq", allocationSize = 1)
    @Column(name = "establishment_id")
    private Long establishmentId;

    @Column(name = "establishment_name", nullable = false, length = 300)
    private String establishmentName;

    @Column(name = "activity_type", length = 200)
    private String activityType;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "latitude", precision = 10)
    private Double latitude;

    @Column(name = "longitude", precision = 10)
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_type_id", foreignKey = @ForeignKey(name = "fk_establishment_inspection_type"))
    private InspectionType inspectionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isic_id", foreignKey = @ForeignKey(name = "fk_establishment_isic"))
    private IsicActivity isicActivity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isic_detail_id", foreignKey = @ForeignKey(name = "fk_establishment_isic_detail"))
    private IsicActivity isicDetail;

    @Column(name = "license_available")
    private Boolean licenseAvailable = Boolean.TRUE;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<License> licenses = new ArrayList<>();

    public Establishment() {}

    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }

    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public InspectionType getInspectionType() { return inspectionType; }
    public void setInspectionType(InspectionType inspectionType) { this.inspectionType = inspectionType; }

    public IsicActivity getIsicActivity() { return isicActivity; }
    public void setIsicActivity(IsicActivity isicActivity) { this.isicActivity = isicActivity; }

    public IsicActivity getIsicDetail() { return isicDetail; }
    public void setIsicDetail(IsicActivity isicDetail) { this.isicDetail = isicDetail; }

    public Boolean getLicenseAvailable() { return licenseAvailable; }
    public void setLicenseAvailable(Boolean licenseAvailable) { this.licenseAvailable = licenseAvailable; }

    public List<License> getLicenses() { return licenses; }
    public void setLicenses(List<License> licenses) { this.licenses = licenses; }
}
