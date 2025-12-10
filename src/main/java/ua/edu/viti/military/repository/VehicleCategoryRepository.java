package ua.edu.viti.military.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.viti.military.entity.VehicleCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleCategoryRepository extends JpaRepository<VehicleCategory, Long> {

    // === ТОЧНИЙ ПОШУК (Для логіки) ===

    // Знайти по назві (ігноруючи регістр, щоб не створювати дублікатів через CapsLock)
    Optional<VehicleCategory> findByNameIgnoreCase(String name);

    Optional<VehicleCategory> findByCodeIgnoreCase(String code);

    // === ПЕРЕВІРКА ІСНУВАННЯ (Для валідації) ===

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    // === ПОШУК ДЛЯ КОРИСТУВАЧА (Фільтрація/Autocomplete) ===

    // Знайти всі категорії, які містять в назві певний текст (напр. пошук "вантаж")
    List<VehicleCategory> findByNameContainingIgnoreCase(String namePart);
}