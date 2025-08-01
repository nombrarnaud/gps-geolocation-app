package com.gps.tracker.repository;

import com.gps.tracker.entity.History;
import com.gps.tracker.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    
    Page<History> findByVehicle(Vehicle vehicle, Pageable pageable);
    
    @Query("SELECT h FROM History h WHERE h.vehicle = :vehicle AND h.timestamp >= :startDate ORDER BY h.timestamp DESC")
    Page<History> findByVehicleAndTimestampAfter(@Param("vehicle") Vehicle vehicle, 
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 Pageable pageable);
    
    @Query("SELECT h FROM History h WHERE h.vehicle = :vehicle AND h.timestamp BETWEEN :startDate AND :endDate ORDER BY h.timestamp DESC")
    List<History> findByVehicleAndTimestampBetween(@Param("vehicle") Vehicle vehicle, 
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT h FROM History h WHERE h.vehicle.owner.id = :userId AND h.timestamp >= :startDate")
    List<History> findByUserIdAndTimestampAfter(@Param("userId") Long userId, 
                                               @Param("startDate") LocalDateTime startDate);
    
    void deleteByVehicle(Vehicle vehicle);
    
    @Query("DELETE FROM History h WHERE h.timestamp < :cutoffDate")
    void deleteOldHistory(@Param("cutoffDate") LocalDateTime cutoffDate);
}
