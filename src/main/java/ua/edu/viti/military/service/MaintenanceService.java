package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.MaintenanceRequestDTO;
import ua.edu.viti.military.dto.request.MaintenanceCompleteRequestDTO; // <-- ДОДАНО (Припускаємо, що ви його створили)
import ua.edu.viti.military.dto.response.MaintenanceResponseDTO;
import ua.edu.viti.military.entity.Maintenance;
import ua.edu.viti.military.entity.Vehicle;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.mapper.MaintenanceMapper;
import ua.edu.viti.military.repository.MaintenanceRepository;
import ua.edu.viti.military.repository.VehicleRepository;
import ua.edu.viti.military.event.MaintenanceCompletedEvent;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceMapper maintenanceMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MaintenanceResponseDTO createMaintenance(MaintenanceRequestDTO dto) {
        // 1. Знаходимо машину
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Машина не знайдена"));

        // 2. Перетворюємо DTO в Entity (автоматично!)
        Maintenance maintenance = maintenanceMapper.toEntity(dto);

        // 3. Дозаповнюємо дані, яких не було в DTO або маппері
        maintenance.setVehicle(vehicle);
        if (maintenance.getMaintenanceDate() == null) {
            maintenance.setMaintenanceDate(LocalDate.now());
        }

        // 4. Оновлюємо статус машини (вона тепер на ремонті)
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        vehicleRepository.save(vehicle);

        // 5. Зберігаємо запис про ТО
        Maintenance saved = maintenanceRepository.save(maintenance);

        // 6. Повертаємо DTO (автоматично!)
        return maintenanceMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponseDTO> getHistoryByVehicle(Long vehicleId) {
        List<Maintenance> logs = maintenanceRepository.findByVehicleIdOrderByMaintenanceDateDesc(vehicleId);
        return maintenanceMapper.toResponseDTOList(logs);
    }

    /**
     * === ОНОВЛЕНО: ЗАВЕРШЕННЯ ТЕХНІЧНОГО ОБСЛУГОВУВАННЯ З DTO І EVENT ===
     */
    @Transactional
    public void completeMaintenance(Long vehicleId, MaintenanceCompleteRequestDTO dto) { // <-- ЗМІНЕНО ПІДПИС
        // 1. Знаходимо машину
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Машина не знайдена"));

        // 2. Перевіряємо, чи машина взагалі на ТО
        if (vehicle.getStatus() != VehicleStatus.MAINTENANCE) {
            throw new RuntimeException("Машина ID=" + vehicleId + " не перебуває на технічному обслуговуванні (Поточний статус: " + vehicle.getStatus().name() + ").");
        }

        // 3. Знаходимо останній незавершений запис ТО для оновлення
        Maintenance latestMaintenance = maintenanceRepository.findByVehicleIdOrderByMaintenanceDateDesc(vehicleId)
                .stream()
                .filter(m -> m.getCompletionDate() == null) // Фільтруємо ті, що ще не завершені
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Не знайдено активного запису ТО для оновлення."));

        // 4. Оновлюємо запис ТО з даними з DTO
        latestMaintenance.setCompletionNotes(dto.getCompletionNotes());
        latestMaintenance.setCompletionDate(LocalDate.now()); // Фіксуємо дату завершення
        Maintenance savedMaintenance = maintenanceRepository.save(latestMaintenance); // Зберігаємо оновлений лог

        // 5. Оновлюємо статус машини
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setLastMaintenanceDate(LocalDate.now());
        vehicleRepository.save(vehicle);

        // 6. ✅ ПУБЛІКАЦІЯ EVENT
        eventPublisher.publishEvent(
                new MaintenanceCompletedEvent(
                        this,
                        savedMaintenance, // Передаємо оновлену сутність
                        "System/Officer"
                )
        );
        log.info("MaintenanceCompletedEvent published for vehicle ID: {}", vehicleId);
    }
}