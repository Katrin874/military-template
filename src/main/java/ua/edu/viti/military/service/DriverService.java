package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO;
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.entity.Driver;
import ua.edu.viti.military.mapper.DriverMapper;
import ua.edu.viti.military.repository.DriverRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper; // Інжектимо маппер

    // === CREATE ===
    @Transactional
    public DriverResponseDTO create(DriverCreateDTO dto) {
        if (driverRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new RuntimeException("Водій з номером прав " + dto.getLicenseNumber() + " вже існує");
        }

        // Використовуємо маппер для перетворення DTO -> Entity
        Driver driver = driverMapper.toEntity(dto);

        Driver saved = driverRepository.save(driver);

        // Повертаємо DTO
        return driverMapper.toResponseDTO(saved);
    }

    // === GET ALL + FILTER ===
    public List<DriverResponseDTO> getAll(Boolean isActive) {
        List<Driver> drivers;

        if (isActive == null) {
            drivers = driverRepository.findAll();
        } else if (isActive) {
            drivers = driverRepository.findByStatus("ACTIVE");
        } else {
            drivers = driverRepository.findByStatus("INACTIVE");
        }

        // Використовуємо маппер для списку
        return driverMapper.toResponseDTOList(drivers);
    }

    // === GET BY ID ===
    public DriverResponseDTO getById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Водій не знайдений з ID: " + id));

        return driverMapper.toResponseDTO(driver);
    }

    // === UPDATE ===
    @Transactional
    public DriverResponseDTO update(Long id, DriverUpdateDTO dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Водій не знайдений з ID: " + id));

        // Використовуємо маппер для оновлення (MapStruct оновлює тільки ті поля, що є в DTO)
        driverMapper.updateEntityFromDTO(dto, driver);

        // Додаткова логіка для статусів (якщо переводимо в INACTIVE)
        if (dto.getIsActive() != null && !dto.getIsActive()) {
            driver.setStatus("INACTIVE");
        } else if (dto.getIsActive() != null && dto.getIsActive()) {
            driver.setStatus("ACTIVE");
        }

        Driver updated = driverRepository.save(driver);
        return driverMapper.toResponseDTO(updated);
    }

    // === DELETE (Деактивація) ===
    @Transactional
    public void delete(Long id) {
        // У бойових системах видаляти не можна, зазвичай змінюють статус
        // Але тут ми просто видаляємо
        driverRepository.deleteById(id);
    }

    // === ЗВІТ (Додаткові бали) ===
    public List<DriverResponseDTO> getDriversWithExpiredLicenses() {
        // Припускаємо, що в Entity Driver є поле licenseExpiryDate
        List<Driver> expired = driverRepository.findByLicenseExpiryDateBefore(LocalDate.now());

        return driverMapper.toResponseDTOList(expired);
    }
}