package com.gps.tracker.service;

import com.gps.tracker.dto.VehicleRequest;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class VehicleService {
    
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HistoryRepository historyRepository;
    
    @Autowired
    private GPSIntegrationService gpsIntegrationService;
    
    public Vehicle createVehicle(VehicleRequest vehicleRequest) {
        User currentUser = getCurrentUser();
        
        Vehicle vehicle = new Vehicle();
        vehicle.setName(vehicleRequest.getName());
        vehicle.setCurrentLatitude(vehicleRequest.getCurrentLatitude());
        vehicle.setCurrentLongitude(vehicleRequest.getCurrentLongitude());
        vehicle.setSpeed(vehicleRequest.getSpeed());
        vehicle.setAltitude(vehicleRequest.getAltitude());
        vehicle.setWeight(vehicleRequest.getWeight());
        vehicle.setOwner(currentUser);
        
        // Update GPS data if coordinates are provided
        if (vehicle.getCurrentLatitude() != null && vehicle.getCurrentLongitude() != null) {
            vehicle = gpsIntegrationService.updateVehicleMetrics(vehicle);
        }
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        // Create initial history entry
        if (savedVehicle.getCurrentLatitude() != null && savedVehicle.getCurrentLongitude() != null) {
            createHistoryEntry(savedVehicle);
        }
        
        logger.info("Vehicle {} created for user {}", savedVehicle.getName(), currentUser.getEmail());
        
        return savedVehicle;
    }
    
    public List<Vehicle> getUserVehicles() {
        User currentUser = getCurrentUser();
        return vehicleRepository.findByOwner(currentUser);
    }
    
    public Page<Vehicle> getUserVehicles(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return vehicleRepository.findByOwner(currentUser, pageable);
    }
    
    public Vehicle getVehicleById(Long vehicleId) {
        User currentUser = getCurrentUser();
        return vehicleRepository.findByIdAndOwner(vehicleId, currentUser)
            .orElseThrow(() -> new RuntimeException("Vehicle not found or access denied"));
    }
    
    public Vehicle updateVehicle(Long vehicleId, VehicleRequest vehicleRequest) {
        Vehicle vehicle = getVehicleById(vehicleId);
        
        vehicle.setName(vehicleRequest.getName());
        if (vehicleRequest.getCurrentLatitude() != null) {
            vehicle.setCurrentLatitude(vehicleRequest.getCurrentLatitude());
        }
        if (vehicleRequest.getCurrentLongitude() != null) {
            vehicle.setCurrentLongitude(vehicleRequest.getCurrentLongitude());
        }
        if (vehicleRequest.getSpeed() != null) {
            vehicle.setSpeed(vehicleRequest.getSpeed());
        }
        if (vehicleRequest.getAltitude() != null) {
            vehicle.setAltitude(vehicleRequest.getAltitude());
        }
        if (vehicleRequest.getWeight() != null) {
            vehicle.setWeight(vehicleRequest.getWeight());
        }
        
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        
        // Create history entry for the update
        createHistoryEntry(updatedVehicle);
        
        logger.info("Vehicle {} updated for user {}", updatedVehicle.getName(), getCurrentUser().getEmail());
        
        return updatedVehicle;
    }
    
    public void deleteVehicle(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        
        // Delete associated history
        historyRepository.deleteByVehicle(vehicle);
        
        // Delete vehicle
        vehicleRepository.delete(vehicle);
        
        logger.info("Vehicle {} deleted for user {}", vehicle.getName(), getCurrentUser().getEmail());
    }
    
    public List<Vehicle> searchVehicles(String name) {
        User currentUser = getCurrentUser();
        return vehicleRepository.findByOwnerAndNameContaining(currentUser, name);
    }
    
    public long getUserVehicleCount() {
        User currentUser = getCurrentUser();
        return vehicleRepository.countByOwner(currentUser);
    }
    
    /**
     * Scheduled task to update GPS data for all vehicles every 30 seconds
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    public void updateAllVehiclesGPSData() {
        logger.info("Starting scheduled GPS data update for all vehicles");
        
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        
        for (Vehicle vehicle : allVehicles) {
            try {
                // Update GPS data
                Vehicle updatedVehicle = gpsIntegrationService.updateVehicleMetrics(vehicle);
                vehicleRepository.save(updatedVehicle);
                
                // Create history entry
                createHistoryEntry(updatedVehicle);
                
            } catch (Exception ex) {
                logger.error("Error updating GPS data for vehicle {}: {}", vehicle.getName(), ex.getMessage());
            }
        }
        
        logger.info("Completed scheduled GPS data update for {} vehicles", allVehicles.size());
    }
    
    /**
     * Scheduled task to clean up old history data (older than 5 months)
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupOldHistory() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(5);
        historyRepository.deleteOldHistory(cutoffDate);
        logger.info("Cleaned up history data older than {}", cutoffDate);
    }
    
    private void createHistoryEntry(Vehicle vehicle) {
        if (vehicle.getCurrentLatitude() != null && vehicle.getCurrentLongitude() != null) {
            History history = new History(
                vehicle,
                vehicle.getCurrentLatitude(),
                vehicle.getCurrentLongitude(),
                vehicle.getSpeed(),
                vehicle.getAltitude(),
                vehicle.getWeight()
            );
            historyRepository.save(history);
        }
    }
    
    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        
        return userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
