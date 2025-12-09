package ua.edu.viti.military.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.viti.military.entity.Driver;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Знайти конкретного водія за військовим квитком
    Optional<Driver> findByMilitaryId(String militaryId);

    // Знайти за номером прав
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    // Знайти всіх активних (хто в строю) або неактивних
    List<Driver> findByIsActive(Boolean isActive);

    // Знайти водіїв, у яких права прострочені (дата закінчення ПЕРЕД заданою датою)
    // Використання: repository.findByLicenseExpiryDateBefore(LocalDate.now());
    List<Driver> findByLicenseExpiryDateBefore(LocalDate date);
}
