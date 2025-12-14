package ua.edu.viti.military.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.viti.military.entity.VehicleCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleCategoryRepository extends JpaRepository<VehicleCategory, Long> {

    // === ТОЧНИЙ ПОШУК (Для логіки) ===
    Optional<VehicleCategory> findByNameIgnoreCase(String name);

    Optional<VehicleCategory> findByCodeIgnoreCase(String code);

    // === ПЕРЕВІРКА ІСНУВАННЯ (Для валідації) ===
    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    // === ПОШУК ДЛЯ КОРИСТУВАЧА (Фільтрація/Autocomplete) ===
    List<VehicleCategory> findByNameContainingIgnoreCase(String namePart);
}