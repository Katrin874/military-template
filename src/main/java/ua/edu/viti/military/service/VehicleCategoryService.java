package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleCategoryCreateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.entity.VehicleCategory;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.VehicleCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleCategoryService {

    private final VehicleCategoryRepository categoryRepository;

    // === CREATE ===
    @Transactional
    public VehicleCategoryResponseDTO create(VehicleCategoryCreateDTO dto) {
        log.info("Creating new category: {}", dto.getName());

        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Категорія з назвою '" + dto.getName() + "' вже існує");
        }

        if (dto.getCode() != null && categoryRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new DuplicateResourceException("Категорія з кодом '" + dto.getCode() + "' вже існує");
        }

        VehicleCategory category = new VehicleCategory();
        category.setName(dto.getName());
        category.setCode(dto.getCode());
        category.setDescription(dto.getDescription());
        category.setRequiredLicense(dto.getRequiredLicense());
        category.setMaxLoadCapacity(dto.getMaxLoadCapacity());

        return toDTO(categoryRepository.save(category));
    }

    // === UPDATE ===
    @Transactional
    public VehicleCategoryResponseDTO update(Long id, VehicleCategoryCreateDTO dto) {
        log.info("Updating category with ID: {}", id);

        VehicleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категорію з ID " + id + " не знайдено"));

        // Перевірка дубліката назви (Тільки якщо назва змінилася!)
        if (!category.getName().equalsIgnoreCase(dto.getName()) &&
                categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Категорія '" + dto.getName() + "' вже існує");
        }

        // Оновлюємо поля
        category.setName(dto.getName());
        category.setCode(dto.getCode());
        category.setDescription(dto.getDescription());
        category.setRequiredLicense(dto.getRequiredLicense());
        category.setMaxLoadCapacity(dto.getMaxLoadCapacity());

        return toDTO(categoryRepository.save(category));
    }

    // === DELETE ===
    @Transactional
    public void delete(Long id) {
        log.info("Deleting category with ID: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Категорію з ID " + id + " не знайдено");
        }

        // Якщо до категорії прив'язані машини, БД викине DataIntegrityViolationException.
        // Це обробить GlobalExceptionHandler (як 500 або 409).
        categoryRepository.deleteById(id);
    }

    // === READ ALL ===
    public List<VehicleCategoryResponseDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === READ BY ID ===
    public VehicleCategoryResponseDTO getById(Long id) {
        VehicleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категорію з ID " + id + " не знайдено"));
        return toDTO(category);
    }

    // Ручний мапер (Entity -> DTO)
    private VehicleCategoryResponseDTO toDTO(VehicleCategory entity) {
        return new VehicleCategoryResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getDescription(),
                entity.getRequiredLicense(),
                entity.getMaxLoadCapacity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}