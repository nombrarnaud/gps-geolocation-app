package com.gps.tracker.controller;

import com.gps.tracker.dto.AuthResponse;
import com.gps.tracker.dto.LoginRequest;
import com.gps.tracker.dto.RegisterRequest;
import com.gps.tracker.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("data", authResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Login failed for email {}: {}", loginRequest.getEmail(), ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid email or password");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String result = authService.register(registerRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception ex) {
            logger.error("Registration failed for email {}: {}", registerRequest.getEmail(), ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Registration failed");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        try {
            boolean exists = authService.existsByEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("available", !exists);
            response.put("message", exists ? "Email is already taken" : "Email is available");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error checking email availability: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error checking email availability");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/check-phone")
    public ResponseEntity<?> checkPhoneAvailability(@RequestParam String phone) {
        try {
            boolean exists = authService.existsByPhone(phone);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("available", !exists);
            response.put("message", exists ? "Phone number is already taken" : "Phone number is available");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error checking phone availability: {}", ex.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error checking phone availability");
            response.put("error", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Since we're using JWT tokens, logout is handled on the client side
        // by removing the token from storage
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken() {
        // If this endpoint is reached, it means the JWT token is valid
        // (due to the security filter chain)
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Token is valid");
        response.put("valid", true);
        
        return ResponseEntity.ok(response);
    }
}
