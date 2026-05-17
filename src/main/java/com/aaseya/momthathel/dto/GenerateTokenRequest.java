package com.aaseya.momthathel.dto;

public class GenerateTokenRequest {

    private String username;
    private Integer validityHours;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getValidityHours() { return validityHours; }
    public void setValidityHours(Integer validityHours) { this.validityHours = validityHours; }
}
