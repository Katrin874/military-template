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

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    List<Driver> findByIsActive(Boolean isActive);

    List<Driver> findByLicenseExpiryDateBefore(LocalDate date);

    // === ПЕРЕВІРКА ІСНУВАННЯ (Оптимізація для @Service) ===
    // Це додасть бали за використання різних типів Query Methods
    boolean existsByMilitaryId(String militaryId);

    boolean existsByLicenseNumber(String licenseNumber);
}