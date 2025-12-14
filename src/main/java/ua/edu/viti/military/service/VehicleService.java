package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleCreateDTO;
import ua.edu.viti.military.dto.request.VehicleUpdateDTO;
// ВИДАЛЕНІ: імпорти DTO, які тепер не потрібні в цьому класі
import ua.edu.viti.military.dto.response.VehicleResponseDTO;
import ua.edu.viti.military.entity.Driver;
import ua.edu.viti.military.entity.Vehicle;
import ua.edu.viti.military.entity.VehicleCategory;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.exception.BusinessLogicException;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.mapper.VehicleMapper; // <--- ДОДАНО: MapStruct Mapper
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
    private final VehicleMapper vehicleMapper; // <--- ДОДАНО: Інжекція маппера

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

        // 4. Створення сутності (замість ручного маппінгу - використовуємо MapStruct)
        // ПРИМІТКА: Оскільки ваш маппер VehicleMapper.toEntity не робить повний маппінг,
        // а лише мапить ID, тут краще використовувати його, але для уникнення помилок
        // у вас ручний маппінг був такий, ми його спростимо за допомогою toEntity,
        // але поля, які він не мапив, додамо вручну, якщо це необхідно.

        // --- Початковий ручний підхід (виправлено додаванням brand) ---
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(dto.getBrand()); // <--- ВИПРАВЛЕНО: Додано відсутнє поле BRAND
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

        // 6. Збереження та маппінг
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Техніку успішно збережено з ID: {}", savedVehicle.getId());

        return vehicleMapper.toDto(savedVehicle); // <--- ВИКОРИСТАННЯ MAPSTRUCT
    }

    // === UPDATE ===
    @Transactional
    public VehicleResponseDTO update(Long id, VehicleUpdateDTO dto) {
        log.info("Оновлення даних техніки ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Машину не знайдено"));

        // ВИКОРИСТАННЯ MAPSTRUCT ДЛЯ ОНОВЛЕННЯ
        vehicleMapper.updateEntity(dto, vehicle);

        // Оновлення водія (логіка, яку маппер не може зробити сам)
        if (dto.getDriverId() != null) {
            assignDriverToVehicle(vehicle, dto.getDriverId());
        }

        return vehicleMapper.toDto(vehicleRepository.save(vehicle)); // <--- ВИКОРИСТАННЯ MAPSTRUCT
    }

    // === READ ALL (З ФІЛЬТРАЦІЄЮ) ===
    public List<VehicleResponseDTO> getAll(VehicleStatus status) {
        List<Vehicle> vehicles;

        if (status != null) {
            vehicles = vehicleRepository.findByStatus(status);
        } else {
            vehicles = vehicleRepository.findAll();
        }

        return vehicles.stream()
                .map(vehicleMapper::toDto) // <--- ВИКОРИСТАННЯ MAPSTRUCT
                .collect(Collectors.toList());
    }

    // === READ ONE ===
    public VehicleResponseDTO getById(Long id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toDto) // <--- ВИКОРИСТАННЯ MAPSTRUCT
                .orElseThrow(() -> new ResourceNotFoundException("Машину з ID " + id + " не знайдено"));
    }

    // === SPECIFIC BUSINESS LOGIC ===
    public List<VehicleResponseDTO> getVehiclesRequiringMaintenance() {
        return vehicleRepository.findVehiclesRequiringMaintenance().stream()
                .map(vehicleMapper::toDto) // <--- ВИКОРИСТАННЯ MAPSTRUCT
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

    // <--- ВИДАЛЕНО: Приватний метод toDTO, який спричиняв помилку конструктора

}