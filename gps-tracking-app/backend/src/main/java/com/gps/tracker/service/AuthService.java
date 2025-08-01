package com.gps.tracker.service;

import com.gps.tracker.dto.AuthResponse;
import com.gps.tracker.dto.LoginRequest;
import com.gps.tracker.dto.RegisterRequest;
import com.gps.tracker.entity.User;
import com.gps.tracker.entity.UserType;
import com.gps.tracker.repository.UserRepository;
import com.gps.tracker.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        logger.info("User {} logged in successfully", user.getEmail());
        
        return new AuthResponse(
            jwt,
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getUserType(),
            tokenProvider.getExpirationTime()
        );
    }
    
    public String register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }
        
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("Phone number is already taken!");
        }
        
        // Validate user type specific fields
        validateUserTypeFields(registerRequest);
        
        // Create new user
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        user.setUserType(registerRequest.getUserType());
        
        // Set type-specific fields
        if (registerRequest.getUserType() == UserType.BUSINESS) {
            user.setCompanyName(registerRequest.getCompanyName());
            user.setRegistrationNumber(registerRequest.getRegistrationNumber());
            user.setManagerName(registerRequest.getManagerName());
        } else if (registerRequest.getUserType() == UserType.SIMPLE) {
            user.setIdCardNumber(registerRequest.getIdCardNumber());
        }
        
        User savedUser = userRepository.save(user);
        
        logger.info("New {} user registered: {}", savedUser.getUserType(), savedUser.getEmail());
        
        return "User registered successfully";
    }
    
    private void validateUserTypeFields(RegisterRequest registerRequest) {
        if (registerRequest.getUserType() == UserType.BUSINESS) {
            if (registerRequest.getCompanyName() == null || registerRequest.getCompanyName().trim().isEmpty()) {
                throw new RuntimeException("Company name is required for business users");
            }
            if (registerRequest.getRegistrationNumber() == null || registerRequest.getRegistrationNumber().trim().isEmpty()) {
                throw new RuntimeException("Registration number is required for business users");
            }
            if (registerRequest.getManagerName() == null || registerRequest.getManagerName().trim().isEmpty()) {
                throw new RuntimeException("Manager name is required for business users");
            }
            
            // Check if registration number already exists
            if (userRepository.existsByRegistrationNumber(registerRequest.getRegistrationNumber())) {
                throw new RuntimeException("Registration number is already taken!");
            }
            
        } else if (registerRequest.getUserType() == UserType.SIMPLE) {
            if (registerRequest.getIdCardNumber() == null || registerRequest.getIdCardNumber().trim().isEmpty()) {
                throw new RuntimeException("ID card number is required for simple users");
            }
            
            // Check if ID card number already exists
            if (userRepository.existsByIdCardNumber(registerRequest.getIdCardNumber())) {
                throw new RuntimeException("ID card number is already taken!");
            }
        }
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
}
