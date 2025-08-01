package com.gps.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public class VehicleRequest {
    
    @NotBlank(message = "Vehicle name is required")
    private String name;
    
    private Double currentLatitude;
    private Double currentLongitude;
    private Double speed;
    private Double altitude;
    private Double weight;
    
    public VehicleRequest() {}
    
    public VehicleRequest(String name, Double currentLatitude, Double currentLongitude, 
                         Double speed, Double altitude, Double weight) {
        this.name = name;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.speed = speed;
        this.altitude = altitude;
        this.weight = weight;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Double getCurrentLatitude() {
        return currentLatitude;
    }
    
    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    
    public Double getCurrentLongitude() {
        return currentLongitude;
    }
    
    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
    
    public Double getSpeed() {
        return speed;
    }
    
    public void setSpeed(Double speed) {
        this.speed = speed;
    }
    
    public Double getAltitude() {
        return altitude;
    }
    
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
