package com.gps.tracker.controller;

import com.gps.tracker.dto.VehicleRequest;
import com.gps.tracker.entity.Vehicle;
import com.gps.tracker.service.VehicleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER')")
public class VehicleController {
    
    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);
    
    @Autowired
    private VehicleService vehicleService;
    
    @PostMapping
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleRequest vehicleRequest) {
        try {
            Vehicle vehicle = vehicleService.createVehicle(vehicleRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle created successfully");
            response.put("data", vehicle);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception ex) {
            logger.error("Error creating vehicle: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create vehicle");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (page < 0 || size <= 0) {
                // Return all vehicles if pagination parameters are invalid
                List<Vehicle> vehicles = vehicleService.getUserVehicles();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Vehicles retrieved successfully");
                response.put("data", vehicles);
                response.put("total", vehicles.size());
                
                return ResponseEntity.ok(response);
            } else {
                // Return paginated vehicles
                Page<Vehicle> vehiclePage = vehicleService.getUserVehicles(page, size);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Vehicles retrieved successfully");
                response.put("data", vehiclePage.getContent());
                response.put("currentPage", vehiclePage.getNumber());
                response.put("totalPages", vehiclePage.getTotalPages());
                response.put("totalElements", vehiclePage.getTotalElements());
                response.put("hasNext", vehiclePage.hasNext());
                response.put("hasPrevious", vehiclePage.hasPrevious());
                
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception ex) {
            logger.error("Error retrieving vehicles: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve vehicles");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle retrieved successfully");
            response.put("data", vehicle);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error retrieving vehicle {}: {}", id, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vehicle not found or access denied");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequest vehicleRequest) {
        try {
            Vehicle vehicle = vehicleService.updateVehicle(id, vehicleRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle updated successfully");
            response.put("data", vehicle);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error updating vehicle {}: {}", id, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update vehicle");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error deleting vehicle {}: {}", id, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete vehicle");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchVehicles(@RequestParam String name) {
        try {
            List<Vehicle> vehicles = vehicleService.searchVehicles(name);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Search completed successfully");
            response.put("data", vehicles);
            response.put("total", vehicles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error searching vehicles: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Search failed");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getUserVehicleCount() {
        try {
            long count = vehicleService.getUserVehicleCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle count retrieved successfully");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error getting vehicle count: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get vehicle count");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
