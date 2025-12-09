package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO; // Додано імпорт
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.entity.Driver;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.DriverRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;

    // === CREATE ===
    @Transactional
    public DriverResponseDTO create(DriverCreateDTO dto) {
        log.info("Registering new driver with Military ID: {}", dto.getMilitaryId());

        if (driverRepository.findByMilitaryId(dto.getMilitaryId()).isPresent()) {
            throw new DuplicateResourceException("Водій з військовим квитком " + dto.getMilitaryId() + " вже існує");
        }

        Driver driver = new Driver();
        driver.setMilitaryId(dto.getMilitaryId());
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setMiddleName(dto.getMiddleName());
        driver.setRank(dto.getRank());
        driver.setLicenseNumber(dto.getLicenseNumber());
        driver.setLicenseCategories(dto.getLicenseCategories());
        driver.setLicenseExpiryDate(dto.getLicenseExpiryDate());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setIsActive(true);

        return toDTO(driverRepository.save(driver));
    }

    // === UPDATE (Додано) ===
    @Transactional
    public DriverResponseDTO update(Long id, DriverUpdateDTO dto) {
        log.info("Updating driver with ID: {}", id);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Водія з ID " + id + " не знайдено"));

        // Оновлюємо поля, якщо вони не null
        if (dto.getRank() != null) driver.setRank(dto.getRank());
        if (dto.getLicenseNumber() != null) driver.setLicenseNumber(dto.getLicenseNumber());
        if (dto.getLicenseCategories() != null) driver.setLicenseCategories(dto.getLicenseCategories());
        if (dto.getLicenseExpiryDate() != null) driver.setLicenseExpiryDate(dto.getLicenseExpiryDate());
        if (dto.getPhoneNumber() != null) driver.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getIsActive() != null) driver.setIsActive(dto.getIsActive());

        // Ім'я та військовий квиток зазвичай рідко змінюють, але можна додати за потребою

        return toDTO(driverRepository.save(driver));
    }

    // === DELETE (Додано) ===
    @Transactional
    public void delete(Long id) {
        log.info("Deleting driver with ID: {}", id);

        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Водія з ID " + id + " не знайдено");
        }

        // Тут може виникнути помилка SQL, якщо у водія є техніка.
        // Це очікувано (захист цілісності даних).
        driverRepository.deleteById(id);
    }

    // === READ ===
    public List<DriverResponseDTO> getAll() {
        return driverRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DriverResponseDTO getById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));
        return toDTO(driver);
    }

    private DriverResponseDTO toDTO(Driver entity) {
        return new DriverResponseDTO(
                entity.getId(),
                entity.getMilitaryId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getRank(),
                entity.getLicenseNumber(),
                entity.getLicenseCategories(),
                entity.getLicenseExpiryDate(),
                entity.getIsActive(),
                entity.getCreatedAt()
        );
    }
}