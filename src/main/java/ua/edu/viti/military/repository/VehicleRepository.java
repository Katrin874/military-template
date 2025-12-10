package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.FuelType;
import ua.edu.viti.military.entity.Vehicle;
import ua.edu.viti.military.entity.VehicleStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // === ПОШУК ===
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    List<Vehicle> findByCategoryId(Long categoryId);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByDriverId(Long driverId);

    List<Vehicle> findByFuelType(FuelType fuelType);

    // === ВАЛІДАЦІЯ (Перевірка існування) ===
    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByEngineNumber(String engineNumber);

    // === ЗВІТИ ТА СКЛАДНІ ЗАПИТИ ===

    // 1. Техніка, що потребує ТО (перепробіг)
    @Query("SELECT v FROM Vehicle v WHERE (v.mileage - COALESCE(v.lastMaintenanceMileage, 0)) >= v.maintenanceIntervalKm")
    List<Vehicle> findVehiclesRequiringMaintenance();

    // 2. Отримати всі машини з завантаженими категоріями та водіями (уникнення N+1)
    @Query("SELECT v FROM Vehicle v JOIN FETCH v.category LEFT JOIN FETCH v.driver")
    List<Vehicle> findAllWithDetails();

    // 3. Пошук по статусу з підвантаженням категорії
    @Query("SELECT v FROM Vehicle v JOIN FETCH v.category WHERE v.status = :status")
    List<Vehicle> findByStatusWithCategory(@Param("status") VehicleStatus status);
}