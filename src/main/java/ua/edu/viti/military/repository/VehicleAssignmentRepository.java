package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.VehicleAssignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleAssignmentRepository extends JpaRepository<VehicleAssignment, Long> {

    // 1. Знайти активний рейс для машини (де дата завершення ще не стоїть)
    // Це критично для блокування видачі зайнятої машини
    @Query("SELECT va FROM VehicleAssignment va WHERE va.vehicle.id = :vehicleId AND va.endTime IS NULL")
    Optional<VehicleAssignment> findActiveByVehicleId(@Param("vehicleId") Long vehicleId);

    // 2. Історія виїздів конкретної машини (спочатку нові)
    List<VehicleAssignment> findByVehicleIdOrderByStartTimeDesc(Long vehicleId);

    // 3. Історія виїздів конкретного водія
    List<VehicleAssignment> findByDriverIdOrderByStartTimeDesc(Long driverId);

    // 4. Пошук виїздів за період часу (для звітів)
    List<VehicleAssignment> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // 5. СТАТИСТИКА: Підрахунок сумарного пробігу за період
    // Ми беремо різницю (endMileage - startMileage) і сумуємо її
    @Query("SELECT SUM(va.endMileage - va.startMileage) FROM VehicleAssignment va " +
            "WHERE va.endTime IS NOT NULL " +
            "AND va.startTime BETWEEN :start AND :end")
    Integer calculateTotalDistance(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);
}