package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleCreateDTO;
import ua.edu.viti.military.dto.request.VehicleUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.dto.response.VehicleResponseDTO;
import ua.edu.viti.military.entity.Driver;
import ua.edu.viti.military.entity.Vehicle;
import ua.edu.viti.military.entity.VehicleCategory;
import ua.edu.viti.military.entity.VehicleStatus; // <--- Не забудь цей імпорт!
import ua.edu.viti.military.exception.BusinessLogicException;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.DriverRepository;
import ua.edu.viti.military.repository.VehicleCategoryRepository;
import ua.edu.viti.military.repository.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository categoryRepository;
    private final DriverRepository driverRepository;

    // === CREATE ===
    @Transactional
    public VehicleResponseDTO create(VehicleCreateDTO dto) {
        log.info("Реєстрація нової техніки: {}", dto.getRegistrationNumber());

        // 1. Перевірка унікальності номерного знаку
        if (vehicleRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new DuplicateResourceException("Транспортний засіб з номером " + dto.getRegistrationNumber() + " вже існує");
        }

        // 2. Перевірка унікальності номера двигуна
        if (vehicleRepository.existsByEngineNumber(dto.getEngineNumber())) {
            throw new DuplicateResourceException("Двигун з номером " + dto.getEngineNumber() + " вже зареєстрований");
        }

        // 3. Пошук категорії (обов'язково)
        VehicleCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категорію з ID " + dto.getCategoryId() + " не знайдено"));

        // 4. Створення сутності
        Vehicle vehicle = new Vehicle();
        vehicle.setModel(dto.getModel());
        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        vehicle.setEngineNumber(dto.getEngineNumber());
        vehicle.setChassisNumber(dto.getChassisNumber());
        vehicle.setMileage(dto.getMileage());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setStatus(dto.getStatus());
        vehicle.setMaintenanceIntervalKm(dto.getMaintenanceIntervalKm());
        vehicle.setLastMaintenanceDate(dto.getLastMaintenanceDate());
        vehicle.setLastMaintenanceMileage(dto.getLastMaintenanceMileage());
        vehicle.setCategory(category);

        // 5. Логіка закріплення водія (якщо передано ID)
        if (dto.getDriverId() != null) {
            assignDriverToVehicle(vehicle, dto.getDriverId());
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Техніку успішно збережено з ID: {}", savedVehicle.getId());

        return toDTO(savedVehicle);
    }

    // === UPDATE ===
    @Transactional
    public VehicleResponseDTO update(Long id, VehicleUpdateDTO dto) {
        log.info("Оновлення даних техніки ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Машину не знайдено"));

        // Оновлюємо тільки ті поля, що прийшли (Patch-style logic)
        if (dto.getMileage() != null) vehicle.setMileage(dto.getMileage());
        if (dto.getStatus() != null) vehicle.setStatus(dto.getStatus());
        if (dto.getLastMaintenanceDate() != null) vehicle.setLastMaintenanceDate(dto.getLastMaintenanceDate());
        if (dto.getLastMaintenanceMileage() != null) vehicle.setLastMaintenanceMileage(dto.getLastMaintenanceMileage());

        // Оновлення водія
        if (dto.getDriverId() != null) {
            assignDriverToVehicle(vehicle, dto.getDriverId());
        }

        return toDTO(vehicleRepository.save(vehicle));
    }

    // === READ ALL (З ФІЛЬТРАЦІЄЮ) ===
    // Змінено сигнатуру методу для підтримки фільтрації в контролері
    public List<VehicleResponseDTO> getAll(VehicleStatus status) {
        List<Vehicle> vehicles;

        if (status != null) {
            // Якщо статус передано - шукаємо тільки конкретні машини (напр. тільки справні)
            vehicles = vehicleRepository.findByStatus(status);
        } else {
            // Якщо статус null - повертаємо абсолютно всі машини
            // (Можна використати vehicleRepository.findAllWithDetails(), якщо ти додав цей метод для оптимізації)
            vehicles = vehicleRepository.findAll();
        }

        return vehicles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === READ ONE ===
    public VehicleResponseDTO getById(Long id) {
        return vehicleRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Машину з ID " + id + " не знайдено"));
    }

    // === SPECIFIC BUSINESS LOGIC ===
    public List<VehicleResponseDTO> getVehiclesRequiringMaintenance() {
        return vehicleRepository.findVehiclesRequiringMaintenance().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === DELETE ===
    @Transactional
    public void delete(Long id) {
        log.info("Видалення техніки з ID: {}", id);

        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Машину з ID " + id + " не знайдено");
        }
        vehicleRepository.deleteById(id);
    }

    // === PRIVATE HELPERS ===

    private void assignDriverToVehicle(Vehicle vehicle, Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Водія з ID " + driverId + " не знайдено"));

        // правило: Не можна призначити неактивного водія
        if (Boolean.FALSE.equals(driver.getIsActive())) {
            throw new BusinessLogicException("Водій " + driver.getLastName() + " має статус 'Не активний' і не може бути призначений.");
        }

        vehicle.setDriver(driver);
    }

    private VehicleResponseDTO toDTO(Vehicle v) {
        // Мапимо категорію
        VehicleCategoryResponseDTO catDTO = new VehicleCategoryResponseDTO(
                v.getCategory().getId(),
                v.getCategory().getName(),
                v.getCategory().getCode(),
                v.getCategory().getDescription(),
                v.getCategory().getRequiredLicense(),
                v.getCategory().getMaxLoadCapacity(),
                v.getCategory().getCreatedAt(),
                v.getCategory().getUpdatedAt()
        );

        // Мапимо дані водія безпечно (null-check)
        Long driverId = (v.getDriver() != null) ? v.getDriver().getId() : null;
        String driverName = (v.getDriver() != null)
                ? v.getDriver().getLastName() + " " + v.getDriver().getFirstName()
                : "Не закріплено";

        return new VehicleResponseDTO(
                v.getId(),
                v.getModel(),
                v.getRegistrationNumber(),
                v.getMileage(),
                v.getFuelType(),
                v.getStatus(),
                catDTO,
                driverId,
                driverName,
                v.getMaintenanceIntervalKm(),
                v.getLastMaintenanceDate(),
                v.getLastMaintenanceMileage(),
                v.getCreatedAt(),
                v.getUpdatedAt()
        );
    }
}