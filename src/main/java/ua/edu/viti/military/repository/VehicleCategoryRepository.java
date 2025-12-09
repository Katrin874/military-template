package ua.edu.viti.military.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.viti.military.entity.VehicleCategory;
import java.util.Optional;

@Repository
public interface VehicleCategoryRepository extends JpaRepository<VehicleCategory, Long> {

    // 1. Пошук на ім'я (напр. "Вантажна")
    // Повертає Optional, бо категорії може не існувати
    Optional<VehicleCategory> findByName(String name);

    // 2. Перевірка існування (щоб не створювати дублікати при збереженні)
    boolean existsByName(String name);

    // 3. Пошук по коду (напр. "TRUCK-UA")
    Optional<VehicleCategory> findByCode(String code);

    // 4. Перевірка існування коду
    boolean existsByCode(String code);
}