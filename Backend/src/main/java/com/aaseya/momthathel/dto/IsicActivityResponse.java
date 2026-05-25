package com.aaseya.momthathel.dto;

public class IsicActivityResponse {

    private Long isicId;
    private String isicCode;
    private String nameEn;
    private String nameAr;
    private Long parentId;
    private boolean active;

    public IsicActivityResponse() {}

    public IsicActivityResponse(Long isicId, String isicCode, String nameEn, String nameAr, Long parentId, boolean active) {
        this.isicId = isicId;
        this.isicCode = isicCode;
        this.nameEn = nameEn;
        this.nameAr = nameAr;
        this.parentId = parentId;
        this.active = active;
    }

    public Long getIsicId() { return isicId; }
    public void setIsicId(Long isicId) { this.isicId = isicId; }

    public String getIsicCode() { return isicCode; }
    public void setIsicCode(String isicCode) { this.isicCode = isicCode; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
