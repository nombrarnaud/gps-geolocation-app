package com.gps.tracker.repository;

import com.gps.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    boolean existsByIdCardNumber(String idCardNumber);
    
    boolean existsByRegistrationNumber(String registrationNumber);
}
