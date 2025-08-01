package com.gps.tracker.service;

import com.gps.tracker.entity.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class GPSIntegrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(GPSIntegrationService.class);
    
    @Value("${app.gps.api.base-url}")
    private String gpsApiBaseUrl;
    
    private final Random random = new Random();
    
    /**
     * Simulates GPS data update for a vehicle using free GPS tracking simulation
     * In a real implementation, this would call external GPS tracking APIs
     */
    public Vehicle updateVehicleMetrics(Vehicle vehicle) {
        try {
            // Simulate GPS data update
            // In production, this would make HTTP calls to actual GPS tracking services
            
            // Generate realistic GPS coordinates around the current position
            Double currentLat = vehicle.getCurrentLatitude();
            Double currentLng = vehicle.getCurrentLongitude();
            
            // If no initial position, set a default location (Paris, France)
            if (currentLat == null || currentLng == null) {
                currentLat = 48.8566 + (random.nextGaussian() * 0.01); // Paris latitude with small variation
                currentLng = 2.3522 + (random.nextGaussian() * 0.01);  // Paris longitude with small variation
            } else {
                // Simulate small movement (within 1km radius)
                currentLat += (random.nextGaussian() * 0.005); // ~500m variation
                currentLng += (random.nextGaussian() * 0.005); // ~500m variation
            }
            
            // Simulate realistic vehicle metrics
            Double speed = Math.max(0, 20 + (random.nextGaussian() * 15)); // 0-50 km/h average
            Double altitude = Math.max(0, 100 + (random.nextGaussian() * 50)); // 0-200m altitude
            Double weight = vehicle.getWeight() != null ? vehicle.getWeight() : 1500 + (random.nextGaussian() * 500); // Vehicle weight
            
            // Update vehicle with new GPS data
            vehicle.setCurrentLatitude(currentLat);
            vehicle.setCurrentLongitude(currentLng);
            vehicle.setSpeed(speed);
            vehicle.setAltitude(altitude);
            vehicle.setWeight(weight);
            vehicle.setLastUpdated(LocalDateTime.now());
            
            logger.info("Updated GPS data for vehicle {}: lat={}, lng={}, speed={} km/h", 
                       vehicle.getName(), currentLat, currentLng, speed);
            
        } catch (Exception ex) {
            logger.error("Error updating GPS data for vehicle {}: {}", vehicle.getName(), ex.getMessage());
            // Return vehicle unchanged if GPS update fails
        }
        
        return vehicle;
    }
    
    /**
     * Simulates fetching GPS data from external API
     * In production, this would call services like:
     * - OpenStreetMap Nominatim API for geocoding
     * - GPS tracking device APIs
     * - Fleet management APIs
     */
    public GPSData fetchGPSData(String deviceId) {
        try {
            // Simulate API call delay
            Thread.sleep(100);
            
            // Generate simulated GPS data
            return new GPSData(
                48.8566 + (random.nextGaussian() * 0.01),
                2.3522 + (random.nextGaussian() * 0.01),
                Math.max(0, 20 + (random.nextGaussian() * 15)),
                Math.max(0, 100 + (random.nextGaussian() * 50)),
                LocalDateTime.now()
            );
            
        } catch (Exception ex) {
            logger.error("Error fetching GPS data for device {}: {}", deviceId, ex.getMessage());
            return null;
        }
    }
    
    /**
     * Validates GPS coordinates
     */
    public boolean isValidGPSCoordinate(Double latitude, Double longitude) {
        return latitude != null && longitude != null &&
               latitude >= -90 && latitude <= 90 &&
               longitude >= -180 && longitude <= 180;
    }
    
    /**
     * Calculates distance between two GPS points using Haversine formula
     */
    public double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (!isValidGPSCoordinate(lat1, lng1) || !isValidGPSCoordinate(lat2, lng2)) {
            return 0.0;
        }
        
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }
    
    /**
     * Inner class for GPS data structure
     */
    public static class GPSData {
        private final Double latitude;
        private final Double longitude;
        private final Double speed;
        private final Double altitude;
        private final LocalDateTime timestamp;
        
        public GPSData(Double latitude, Double longitude, Double speed, Double altitude, LocalDateTime timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.speed = speed;
            this.altitude = altitude;
            this.timestamp = timestamp;
        }
        
        // Getters
        public Double getLatitude() { return latitude; }
        public Double getLongitude() { return longitude; }
        public Double getSpeed() { return speed; }
        public Double getAltitude() { return altitude; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
