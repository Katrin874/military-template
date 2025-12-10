package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO;
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.entity.Driver;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
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

    // === CREATE ===
    @Transactional
    public DriverResponseDTO create(DriverCreateDTO dto) {
        log.info("Реєстрація водія: {} {}", dto.getLastName(), dto.getFirstName());

        if (driverRepository.existsByMilitaryId(dto.getMilitaryId())) {
            throw new DuplicateResourceException("Водій з військовим квитком " + dto.getMilitaryId() + " вже існує");
        }

        if (driverRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new DuplicateResourceException("Водійське посвідчення " + dto.getLicenseNumber() + " вже зареєстроване");
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

    // === UPDATE ===
    @Transactional
    public DriverResponseDTO update(Long id, DriverUpdateDTO dto) {
        log.info("Оновлення даних водія ID: {}", id);

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Водія з ID " + id + " не знайдено"));

        if (dto.getRank() != null) driver.setRank(dto.getRank());
        if (dto.getLicenseNumber() != null) driver.setLicenseNumber(dto.getLicenseNumber());
        if (dto.getLicenseCategories() != null) driver.setLicenseCategories(dto.getLicenseCategories());
        if (dto.getLicenseExpiryDate() != null) driver.setLicenseExpiryDate(dto.getLicenseExpiryDate());
        if (dto.getPhoneNumber() != null) driver.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getIsActive() != null) driver.setIsActive(dto.getIsActive());

        return toDTO(driverRepository.save(driver));
    }

    // === DELETE ===
    @Transactional
    public void delete(Long id) {
        log.info("Видалення водія ID: {}", id);

        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Водія з ID " + id + " не знайдено");
        }
        driverRepository.deleteById(id);
    }

    // === READ ALL ===
    public List<DriverResponseDTO> getAll(Boolean isActive) {
        List<Driver> drivers;

        if (isActive != null) {
            drivers = driverRepository.findByIsActive(isActive);
        } else {
            drivers = driverRepository.findAll();
        }

        return drivers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === READ ONE ===
    public DriverResponseDTO getById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));
        return toDTO(driver);
    }

    // === SPECIFIC REPORT ===
    public List<DriverResponseDTO> getDriversWithExpiredLicenses() {
        return driverRepository.findByLicenseExpiryDateBefore(LocalDate.now())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === MAPPER (Виправлено) ===
    private DriverResponseDTO toDTO(Driver entity) {
        return new DriverResponseDTO(
                entity.getId(),
                entity.getMilitaryId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getMiddleName(), // <--- ДОДАНО: По-батькові (було пропущено)
                entity.getRank(),
                entity.getLicenseNumber(),
                entity.getLicenseCategories(),
                entity.getLicenseExpiryDate(),
                entity.getIsActive(),
                entity.getCreatedAt()
        );
    }
}