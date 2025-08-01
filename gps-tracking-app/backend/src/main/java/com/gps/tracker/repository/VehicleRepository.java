package com.gps.tracker.repository;

import com.gps.tracker.entity.User;
import com.gps.tracker.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByOwner(User owner);
    
    Page<Vehicle> findByOwner(User owner, Pageable pageable);
    
    Optional<Vehicle> findByIdAndOwner(Long id, User owner);
    
    @Query("SELECT v FROM Vehicle v WHERE v.owner = :owner AND v.name LIKE %:name%")
    List<Vehicle> findByOwnerAndNameContaining(@Param("owner") User owner, @Param("name") String name);
    
    long countByOwner(User owner);
}
