package com.gps.tracker.service;

import com.gps.tracker.entity.History;
import com.gps.tracker.entity.User;
import com.gps.tracker.entity.Vehicle;
import com.gps.tracker.repository.HistoryRepository;
import com.gps.tracker.repository.UserRepository;
import com.gps.tracker.repository.VehicleRepository;
import com.gps.tracker.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class HistoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);
    
    @Autowired
    private HistoryRepository historyRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get paginated history for a specific vehicle (last 5 months)
     */
    public Page<History> getVehicleHistory(Long vehicleId, int page, int size) {
        User currentUser = getCurrentUser();
        
        // Verify vehicle ownership
        Vehicle vehicle = vehicleRepository.findByIdAndOwner(vehicleId, currentUser)
            .orElseThrow(() -> new RuntimeException("Vehicle not found or access denied"));
        
        // Get history from last 5 months
        LocalDateTime fiveMonthsAgo = LocalDateTime.now().minusMonths(5);
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        
        return historyRepository.findByVehicleAndTimestampAfter(vehicle, fiveMonthsAgo, pageable);
    }
    
    /**
     * Get all history for a specific vehicle within date range
     */
    public List<History> getVehicleHistoryByDateRange(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = getCurrentUser();
        
        // Verify vehicle ownership
        Vehicle vehicle = vehicleRepository.findByIdAndOwner(vehicleId, currentUser)
            .orElseThrow(() -> new RuntimeException("Vehicle not found or access denied"));
        
        // Ensure we don't go beyond 5 months
        LocalDateTime fiveMonthsAgo = LocalDateTime.now().minusMonths(5);
        if (startDate.isBefore(fiveMonthsAgo)) {
            startDate = fiveMonthsAgo;
        }
        
        return historyRepository.findByVehicleAndTimestampBetween(vehicle, startDate, endDate);
    }
    
    /**
     * Get recent history for all user's vehicles
     */
    public List<History> getUserRecentHistory(int days) {
        User currentUser = getCurrentUser();
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        return historyRepository.findByUserIdAndTimestampAfter(currentUser.getId(), startDate);
    }
    
    /**
     * Get vehicle movement statistics
     */
    public VehicleStats getVehicleStats(Long vehicleId) {
        User currentUser = getCurrentUser();
        
        // Verify vehicle ownership
        Vehicle vehicle = vehicleRepository.findByIdAndOwner(vehicleId, currentUser)
            .orElseThrow(() -> new RuntimeException("Vehicle not found or access denied"));
        
        // Get last 30 days of history
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<History> recentHistory = historyRepository.findByVehicleAndTimestampBetween(
            vehicle, thirtyDaysAgo, LocalDateTime.now());
        
        if (recentHistory.isEmpty()) {
            return new VehicleStats(0.0, 0.0, 0.0, 0.0, 0);
        }
        
        // Calculate statistics
        double totalDistance = 0.0;
        double maxSpeed = 0.0;
        double avgSpeed = 0.0;
        double avgAltitude = 0.0;
        int totalEntries = recentHistory.size();
        
        History previousHistory = null;
        double speedSum = 0.0;
        double altitudeSum = 0.0;
        
        for (History history : recentHistory) {
            // Calculate distance if we have previous point
            if (previousHistory != null) {
                double distance = calculateDistance(
                    previousHistory.getLatitude(), previousHistory.getLongitude(),
                    history.getLatitude(), history.getLongitude()
                );
                totalDistance += distance;
            }
            
            // Track max speed
            if (history.getSpeed() != null && history.getSpeed() > maxSpeed) {
                maxSpeed = history.getSpeed();
            }
            
            // Sum for averages
            if (history.getSpeed() != null) {
                speedSum += history.getSpeed();
            }
            if (history.getAltitude() != null) {
                altitudeSum += history.getAltitude();
            }
            
            previousHistory = history;
        }
        
        avgSpeed = speedSum / totalEntries;
        avgAltitude = altitudeSum / totalEntries;
        
        return new VehicleStats(totalDistance, maxSpeed, avgSpeed, avgAltitude, totalEntries);
    }
    
    /**
     * Get vehicle route for map display
     */
    public List<RoutePoint> getVehicleRoute(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = getCurrentUser();
        
        // Verify vehicle ownership
        Vehicle vehicle = vehicleRepository.findByIdAndOwner(vehicleId, currentUser)
            .orElseThrow(() -> new RuntimeException("Vehicle not found or access denied"));
        
        List<History> historyList = historyRepository.findByVehicleAndTimestampBetween(vehicle, startDate, endDate);
        
        return historyList.stream()
            .map(h -> new RoutePoint(h.getLatitude(), h.getLongitude(), h.getTimestamp(), h.getSpeed()))
            .toList();
    }
    
    private double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
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
    
    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        
        return userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /**
     * Vehicle statistics data class
     */
    public static class VehicleStats {
        private final double totalDistance;
        private final double maxSpeed;
        private final double avgSpeed;
        private final double avgAltitude;
        private final int totalEntries;
        
        public VehicleStats(double totalDistance, double maxSpeed, double avgSpeed, double avgAltitude, int totalEntries) {
            this.totalDistance = totalDistance;
            this.maxSpeed = maxSpeed;
            this.avgSpeed = avgSpeed;
            this.avgAltitude = avgAltitude;
            this.totalEntries = totalEntries;
        }
        
        // Getters
        public double getTotalDistance() { return totalDistance; }
        public double getMaxSpeed() { return maxSpeed; }
        public double getAvgSpeed() { return avgSpeed; }
        public double getAvgAltitude() { return avgAltitude; }
        public int getTotalEntries() { return totalEntries; }
    }
    
    /**
     * Route point data class for map display
     */
    public static class RoutePoint {
        private final double latitude;
        private final double longitude;
        private final LocalDateTime timestamp;
        private final Double speed;
        
        public RoutePoint(double latitude, double longitude, LocalDateTime timestamp, Double speed) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
            this.speed = speed;
        }
        
        // Getters
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Double getSpeed() { return speed; }
    }
}
