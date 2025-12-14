package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching; // <-- ДОДАНО: Для об'єднання операцій кешу
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleCategoryCreateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.entity.VehicleCategory;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.mapper.VehicleCategoryMapper;
import ua.edu.viti.military.repository.VehicleCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleCategoryService {

    private final VehicleCategoryRepository categoryRepository;
    private final VehicleCategoryMapper categoryMapper;

    // === CREATE ===
    @Transactional
    @CacheEvict(value = "vehicleCategories", key = "'all'") // Скидаємо кеш списку
    public VehicleCategoryResponseDTO create(VehicleCategoryCreateDTO dto) {
        log.info("Creating new category: {}", dto.getName());

        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Категорія з назвою '" + dto.getName() + "' вже існує");
        }

        if (dto.getCode() != null && categoryRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new DuplicateResourceException("Категорія з кодом '" + dto.getCode() + "' вже існує");
        }

        // MapStruct: Перетворюємо DTO в Entity
        VehicleCategory category = categoryMapper.toEntity(dto);

        VehicleCategory saved = categoryRepository.save(category);

        // MapStruct: Перетворюємо Entity в DTO
        return categoryMapper.toResponseDTO(saved);
    }

    // === UPDATE ===
    @Transactional
    @Caching(evict = { // ✅ ВИПРАВЛЕНО: Об'єднуємо два CacheEvict в один @Caching
            @CacheEvict(value = "vehicleCategories", key = "#id"),
            @CacheEvict(value = "vehicleCategories", key = "'all'")
    })
    public VehicleCategoryResponseDTO update(Long id, VehicleCategoryCreateDTO dto) {
        log.info("Updating category with ID: {}", id);

        VehicleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категорію з ID " + id + " не знайдено"));

        // Перевірка дубліката назви (Тільки якщо назва змінилася!)
        if (!category.getName().equalsIgnoreCase(dto.getName()) &&
                categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Категорія '" + dto.getName() + "' вже існує");
        }

        // MapStruct: Оновлюємо Entity з DTO
        categoryMapper.updateEntityFromDTO(dto, category);

        return categoryMapper.toResponseDTO(categoryRepository.save(category));
    }

    // === DELETE ===
    @Transactional
    @CacheEvict(value = "vehicleCategories", allEntries = true) // Скидаємо весь кеш категорій
    public void delete(Long id) {
        log.info("Deleting category with ID: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Категорію з ID " + id + " не знайдено");
        }

        categoryRepository.deleteById(id);
    }

    // === READ ALL ===
    @Cacheable(value = "vehicleCategories", key = "'all'") // Кешуємо список
    public List<VehicleCategoryResponseDTO> getAll() {
        log.info("Fetching all categories from database (potential cache miss)");

        // MapStruct: Перетворюємо List<Entity> в List<DTO>
        return categoryMapper.toResponseDTOList(categoryRepository.findAll());
    }

    // === READ BY ID ===
    @Cacheable(value = "vehicleCategories", key = "#id") // Кешуємо по ID
    public VehicleCategoryResponseDTO getById(Long id) {
        log.info("Fetching category by ID: {} (potential cache miss)", id);

        VehicleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категорію з ID " + id + " не знайдено"));

        // MapStruct: Перетворюємо Entity в DTO
        return categoryMapper.toResponseDTO(category);
    }
}