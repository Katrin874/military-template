package ua.edu.viti.military.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.viti.military.entity.Driver;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // === ПОШУК ===
    Optional<Driver> findByMilitaryId(String militaryId);

    // Отримати водія за номером прав
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    // 👇 ВИПРАВЛЕНО: Ми маємо фільтрувати по полю 'status', яке є String.
    // У DriverService ми будемо передавати "ACTIVE" або "INACTIVE".
    List<Driver> findByStatus(String status);

    // Знайти водіїв, чиї права закінчуються до вказаної дати
    List<Driver> findByLicenseExpiryDateBefore(LocalDate date);

    // === ПЕРЕВІРКА ІСНУВАННЯ (Оптимізація для @Service) ===
    boolean existsByMilitaryId(String militaryId);

    boolean existsByLicenseNumber(String licenseNumber);
}