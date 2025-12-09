package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleAssignmentRequestDTO;
import ua.edu.viti.military.dto.response.VehicleAssignmentResponseDTO;
import ua.edu.viti.military.entity.Driver;
import ua.edu.viti.military.entity.Vehicle;
import ua.edu.viti.military.entity.VehicleAssignment;
import ua.edu.viti.military.exception.BusinessLogicException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.DriverRepository;
import ua.edu.viti.military.repository.VehicleAssignmentRepository;
import ua.edu.viti.military.repository.VehicleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleAssignmentService {

    private final VehicleAssignmentRepository assignmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public VehicleAssignmentResponseDTO assignVehicle(VehicleAssignmentRequestDTO request) {
        // 1. Пошук сутностей
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Водія з ID " + request.getDriverId() + " не знайдено"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Транспорт з ID " + request.getVehicleId() + " не знайдено"));

        // 2.Перевірка статусу водія
        if (Boolean.FALSE.equals(driver.getIsActive())) {
            throw new BusinessLogicException("Водій " + driver.getLastName() + " має статус 'Не активний' і не може бути допущений до керування.");
        }

        // 3.Перевірка терміну дії прав
        if (driver.getLicenseExpiryDate() != null && driver.getLicenseExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessLogicException("Термін дії водійського посвідчення сплив (" + driver.getLicenseExpiryDate() + ")");
        }

        // 4.Перевірка сумісності категорій
        // Логіка: Якщо у машини категорія "C", то у водія в рядку "B,C,CE" має бути "C"
        // Якщо у VehicleCategory назви повні ("Вантажна"), то тут треба буде маппінг.
        String requiredCategory = vehicle.getCategory().getName(); // Наприклад "C"
        if (!isCategoryCompatible(driver.getLicenseCategories(), requiredCategory)) {
            throw new BusinessLogicException(
                    String.format("Водій не має відповідної категорії. Потрібна: %s, Наявні: %s",
                            requiredCategory, driver.getLicenseCategories()));
        }

        // 5.Перевірка ТО
        validateMaintenance(vehicle);

        // 6. Перевірка, чи машина не зайнята іншим водієм
        if (assignmentRepository.findByVehicleIdAndIsActiveTrue(vehicle.getId()).isPresent()) {
            throw new BusinessLogicException("Транспортний засіб " + vehicle.getRegistrationNumber() + " вже знаходиться у використанні.");
        }

        // 7. Створення запису про призначення
        VehicleAssignment assignment = new VehicleAssignment(driver, vehicle);
        VehicleAssignment savedAssignment = assignmentRepository.save(assignment);

        return mapToDTO(savedAssignment);
    }

    @Transactional
    public void completeAssignment(Long assignmentId, Integer finalMileage) {
        VehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Призначення не знайдено"));

        if (!assignment.isActive()) {
            throw new BusinessLogicException("Це призначення вже було завершено раніше.");
        }

        Vehicle vehicle = assignment.getVehicle();

        // Валідація пробігу
        if (finalMileage < vehicle.getMileage()) {
            throw new BusinessLogicException("Новий пробіг (" + finalMileage + ") не може бути меншим за попередній (" + vehicle.getMileage() + ")");
        }

        // Оновлення даних
        vehicle.setMileage(finalMileage); // Оновлюємо загальний пробіг авто

        assignment.setEndDate(LocalDateTime.now());
        assignment.setActive(false);

        vehicleRepository.save(vehicle);
        assignmentRepository.save(assignment);
    }

    private void validateMaintenance(Vehicle vehicle) {
        int currentMileage = vehicle.getMileage();
        int lastServiceMileage = vehicle.getLastMaintenanceMileage();
        int interval = vehicle.getMaintenanceIntervalKm();

        // Якщо різниця між поточним пробігом і останнім ТО більша за інтервал
        if ((currentMileage - lastServiceMileage) > interval) {
            int overdueKm = (currentMileage - lastServiceMileage) - interval;
            throw new BusinessLogicException(
                    String.format("Експлуатація заборонена! Прострочено ТО на %d км. (Інтервал: %d км)",
                            overdueKm, interval));
        }
    }

    private boolean isCategoryCompatible(String driverCategories, String requiredCategory) {
        if (driverCategories == null || driverCategories.isEmpty()) {
            return false;
        }
        // Розбиваємо рядок "B,C,CE" на масив і перевіряємо наявність потрібної категорії
        // Використовуємо trim(), щоб прибрати зайві пробіли після коми
        List<String> categories = Arrays.stream(driverCategories.split(","))
                .map(String::trim)
                .toList();

        return categories.contains(requiredCategory);
    }

    private VehicleAssignmentResponseDTO mapToDTO(VehicleAssignment entity) {
        return VehicleAssignmentResponseDTO.builder()
                .id(entity.getId())
                .driverName(entity.getDriver().getFirstName() + " " + entity.getDriver().getLastName())
                .vehicleModel(entity.getVehicle().getModel())
                .registrationNumber(entity.getVehicle().getRegistrationNumber())
                .startDate(entity.getStartDate())
                .isActive(entity.isActive())
                .build();
    }
}