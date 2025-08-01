package com.gps.tracker.controller;

import com.gps.tracker.entity.User;
import com.gps.tracker.repository.UserRepository;
import com.gps.tracker.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER')")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() {
        try {
            User currentUser = getCurrentUser();
            
            // Remove sensitive information
            currentUser.setPassword(null);
            
            return ResponseEntity.ok(currentUser);
            
        } catch (Exception ex) {
            logger.error("Error retrieving user profile: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(@RequestBody Map<String, Object> updates) {
        try {
            User currentUser = getCurrentUser();
            
            // Update allowed fields
            if (updates.containsKey("fullName")) {
                String fullName = (String) updates.get("fullName");
                if (fullName != null && !fullName.trim().isEmpty()) {
                    currentUser.setFullName(fullName.trim());
                }
            }
            
            if (updates.containsKey("phone")) {
                String phone = (String) updates.get("phone");
                if (phone != null && !phone.trim().isEmpty()) {
                    // Check if phone is already taken by another user
                    if (userRepository.existsByPhone(phone) && !phone.equals(currentUser.getPhone())) {
                        return ResponseEntity.badRequest().build();
                    }
                    currentUser.setPhone(phone.trim());
                }
            }
            
            // Update business-specific fields if user is business type
            if (currentUser.getUserType().name().equals("BUSINESS")) {
                if (updates.containsKey("companyName")) {
                    String companyName = (String) updates.get("companyName");
                    if (companyName != null && !companyName.trim().isEmpty()) {
                        currentUser.setCompanyName(companyName.trim());
                    }
                }
                
                if (updates.containsKey("managerName")) {
                    String managerName = (String) updates.get("managerName");
                    if (managerName != null && !managerName.trim().isEmpty()) {
                        currentUser.setManagerName(managerName.trim());
                    }
                }
            }
            
            User updatedUser = userRepository.save(currentUser);
            
            // Remove sensitive information
            updatedUser.setPassword(null);
            
            return ResponseEntity.ok(updatedUser);
            
        } catch (Exception ex) {
            logger.error("Error updating user profile: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        try {
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");
            
            if (currentPassword == null || newPassword == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Current password and new password are required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (newPassword.length() < 6) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "New password must be at least 6 characters long");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            User currentUser = getCurrentUser();
            
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Current password is incorrect");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Update password
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password changed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error changing password: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to change password");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@RequestBody Map<String, String> confirmationData) {
        try {
            String password = confirmationData.get("password");
            String confirmation = confirmationData.get("confirmation");
            
            if (password == null || !"DELETE".equals(confirmation)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Password and confirmation required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            User currentUser = getCurrentUser();
            
            // Verify password
            if (!passwordEncoder.matches(password, currentUser.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Password is incorrect");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Delete user account (this will cascade delete vehicles and history)
            userRepository.delete(currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error deleting account: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete account");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        
        return userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
