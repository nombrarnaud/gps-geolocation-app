package com.gps.tracker.controller;

import com.gps.tracker.entity.History;
import com.gps.tracker.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER')")
public class HistoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    
    @Autowired
    private HistoryService historyService;
    
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<?> getVehicleHistory(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<History> historyPage = historyService.getVehicleHistory(vehicleId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle history retrieved successfully");
            response.put("data", historyPage.getContent());
            response.put("currentPage", historyPage.getNumber());
            response.put("totalPages", historyPage.getTotalPages());
            response.put("totalElements", historyPage.getTotalElements());
            response.put("hasNext", historyPage.hasNext());
            response.put("hasPrevious", historyPage.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error retrieving vehicle history for vehicle {}: {}", vehicleId, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve vehicle history");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/range")
    public ResponseEntity<?> getVehicleHistoryByDateRange(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<History> history = historyService.getVehicleHistoryByDateRange(vehicleId, startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle history retrieved successfully");
            response.put("data", history);
            response.put("total", history.size());
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error retrieving vehicle history by date range for vehicle {}: {}", vehicleId, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve vehicle history");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<?> getUserRecentHistory(@RequestParam(defaultValue = "7") int days) {
        try {
            if (days <= 0 || days > 30) {
                days = 7; // Default to 7 days if invalid
            }
            
            List<History> history = historyService.getUserRecentHistory(days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Recent history retrieved successfully");
            response.put("data", history);
            response.put("total", history.size());
            response.put("days", days);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error retrieving recent history: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve recent history");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/stats")
    public ResponseEntity<?> getVehicleStats(@PathVariable Long vehicleId) {
        try {
            HistoryService.VehicleStats stats = historyService.getVehicleStats(vehicleId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle statistics retrieved successfully");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error retrieving vehicle stats for vehicle {}: {}", vehicleId, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve vehicle statistics");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/route")
    public ResponseEntity<?> getVehicleRoute(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<HistoryService.RoutePoint> route = historyService.getVehicleRoute(vehicleId, startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle route retrieved successfully");
            response.put("data", route);
            response.put("total", route.size());
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error retrieving vehicle route for vehicle {}: {}", vehicleId, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve vehicle route");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/last-week")
    public ResponseEntity<?> getVehicleLastWeekHistory(@PathVariable Long vehicleId) {
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusWeeks(1);
            
            List<History> history = historyService.getVehicleHistoryByDateRange(vehicleId, startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Last week vehicle history retrieved successfully");
            response.put("data", history);
            response.put("total", history.size());
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error retrieving last week history for vehicle {}: {}", vehicleId, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve last week history");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/last-month")
    public ResponseEntity<?> getVehicleLastMonthHistory(@PathVariable Long vehicleId) {
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusMonths(1);
            
            List<History> history = historyService.getVehicleHistoryByDateRange(vehicleId, startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Last month vehicle history retrieved successfully");
            response.put("data", history);
            response.put("total", history.size());
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error retrieving last month history for vehicle {}: {}", vehicleId, ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve last month history");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
