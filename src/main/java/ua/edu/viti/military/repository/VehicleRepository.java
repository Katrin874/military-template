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

    // 1. Пошук по держ. номеру
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    // 2. Перевірка існування (для валідації при створенні)
    boolean existsByRegistrationNumber(String registrationNumber);

    // 3. Перевірка унікальності двигуна (Додано для VehicleService)
    boolean existsByEngineNumber(String engineNumber);

    // 4. Пошук всіх машин певної категорії
    List<Vehicle> findByCategoryId(Long categoryId);

    // 5. Пошук по статусу
    List<Vehicle> findByStatus(VehicleStatus status);

    long countByStatus(VehicleStatus status);

    // === СПЕЦИФІЧНІ МЕТОДИ ВАРІАНТУ B ===

    // Знайти машини, закріплені за конкретним водієм
    List<Vehicle> findByDriverId(Long driverId);

    // Знайти по типу палива
    List<Vehicle> findByFuelType(FuelType fuelType);

    // Знайти машини з пробігом в діапазоні
    List<Vehicle> findByMileageBetween(Integer minMileage, Integer maxMileage);

    // === СКЛАДНІ ЗАПИТИ (@Query) ===

    // 1. Знайти транспорт, що потребує ТО
    // Логіка: (Поточний пробіг - Пробіг останнього ТО) >= Інтервал ТО
    // COALESCE(v.lastMaintenanceMileage, 0) означає: якщо ТО ще не було (null), вважати 0.
    @Query("SELECT v FROM Vehicle v WHERE " +
            "(v.mileage - COALESCE(v.lastMaintenanceMileage, 0)) >= v.maintenanceIntervalKm")
    List<Vehicle> findVehiclesRequiringMaintenance();

    // 2. Оптимізований пошук зі статусом (Join Fetch Category)
    // Запобігає проблемі N+1 для категорій
    @Query("SELECT v FROM Vehicle v JOIN FETCH v.category WHERE v.status = :status")
    List<Vehicle> findByStatusWithCategory(@Param("status") VehicleStatus status);

    // 3. метод для списків (All in One)
    // Витягує Машину + Категорію + Водія одним запитом.
    // Використовуємо LEFT JOIN для driver, бо водія може не бути (null),
    // і ми не хочемо втратити такі машини зі списку.
    @Query("SELECT v FROM Vehicle v " +
            "JOIN FETCH v.category " +
            "LEFT JOIN FETCH v.driver")
    List<Vehicle> findAllWithDetails();
}