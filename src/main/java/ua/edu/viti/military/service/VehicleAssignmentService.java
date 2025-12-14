package ua.edu.viti.military.service;

import io.micrometer.core.instrument.Timer; // Додано для Timer.Sample
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleAssignmentRequestDTO;
import ua.edu.viti.military.dto.request.VehicleReturnRequestDTO;
import ua.edu.viti.military.dto.response.VehicleAssignmentResponseDTO;
import ua.edu.viti.military.entity.*;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.*;
import ua.edu.viti.military.event.VehicleAssignedEvent;
import ua.edu.viti.military.mapper.VehicleAssignmentMapper;

import org.slf4j.MDC; // <-- ДОДАНО для Structured Logging
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit; // ДОДАНО для Timer

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleAssignmentService {

    private final VehicleAssignmentRepository assignmentRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final VehicleAssignmentMapper assignmentMapper;
    private final MetricsService metricsService; // <-- ІНЖЕКЦІЯ METRICS SERVICE

    /**
     * === ПОЧАТОК РЕЙСУ (Start Assignment) ===
     */
    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = Exception.class
    )
    public VehicleAssignmentResponseDTO startAssignment(VehicleAssignmentRequestDTO request) {

        // 1. Встановлюємо контекст MDC для логування
        MDC.put("operation", "START_ASSIGNMENT");
        MDC.put("vehicleId", request.getVehicleId().toString());

        // Вимірюємо тривалість всієї операції
        return metricsService.measureAssignmentOperation(() -> {
            try {
                log.info("Спроба відправити машину ID={} у рейс", request.getVehicleId());

                // 1. Знаходимо машину з БЛОКУВАННЯМ
                Vehicle vehicle = vehicleRepository.findByIdWithLock(request.getVehicleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Машину не знайдено"));

                // 2. Валідація статусу та зайнятості
                if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
                    metricsService.recordAssignmentFailed(); // <-- ЛІЧИМО НЕСПРАВНІСТЬ
                    throw new RuntimeException("Машина не доступна для виїзду (Статус: " + vehicle.getStatus().name() + ")");
                }
                if (assignmentRepository.findActiveByVehicleId(vehicle.getId()).isPresent()) {
                    metricsService.recordAssignmentFailed(); // <-- ЛІЧИМО НЕСПРАВНІСТЬ
                    throw new RuntimeException("Ця машина вже перебуває у рейсі!");
                }
                // 3. Валідація пробігу
                if (request.getStartMileage() < vehicle.getMileage()) {
                    metricsService.recordAssignmentFailed(); // <-- ЛІЧИМО НЕСПРАВНІСТЬ
                    throw new RuntimeException("Пробіг на виїзді менший за поточний пробіг машини!");
                }

                // 4. Створення сутності з DTO (MapStruct)
                VehicleAssignment assignment = assignmentMapper.toEntity(request);

                // 5. Оновлюємо сутність
                Driver driver = driverRepository.findById(request.getDriverId())
                        .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));

                assignment.setVehicle(vehicle);
                assignment.setDriver(driver);

                assignment.setStatus(AssignmentStatus.ACTIVE);

                // 6. Оновлюємо статус машини
                vehicle.setStatus(VehicleStatus.IN_USE);
                vehicle.setMileage(request.getStartMileage());
                vehicleRepository.save(vehicle);

                VehicleAssignment saved = assignmentRepository.save(assignment);
                log.info("Успішний виїзд: ID={}", saved.getId());

                // 7. Публікація Events та Metrics
                metricsService.recordAssignmentStarted(); // <-- ЛІЧИМО УСПІХ
                eventPublisher.publishEvent(
                        new VehicleAssignedEvent(this, saved.getVehicle(), saved.getDriver().getFullName(), "Підрозділ N", "System Admin")
                );
                log.info("Event VehicleAssignedEvent published.");

                return assignmentMapper.toResponseDTO(saved);
            } finally {
                // Очищаємо MDC після обробки запиту
                MDC.remove("operation");
                MDC.remove("vehicleId");
            }
        });
    }

    /**
     * === ЗАВЕРШЕННЯ РЕЙСУ (Complete Assignment) ===
     */
    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = Exception.class
    )
    public VehicleAssignmentResponseDTO completeAssignment(Long vehicleId, VehicleReturnRequestDTO request) {
        // 1. Встановлюємо контекст MDC для логування
        MDC.put("operation", "COMPLETE_ASSIGNMENT");
        MDC.put("vehicleId", vehicleId.toString());

        try {
            log.info("Завершення рейсу для машини ID={}", vehicleId);

            // 1. Блокуємо машину
            Vehicle vehicle = vehicleRepository.findByIdWithLock(vehicleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Машину не знайдено"));

            // 2. Шукаємо активний рейс
            VehicleAssignment assignment = assignmentRepository.findActiveByVehicleId(vehicleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Активного рейсу немає"));

            // 3. Валідація пробігу
            if (request.getReturnMileage() < assignment.getStartMileage()) {
                throw new RuntimeException("Помилка: Кінцевий пробіг менший за пробіг на виїзді");
            }

            // 4. Закриваємо рейс
            assignment.setEndTime(request.getReturnDate());
            assignment.setEndMileage(request.getReturnMileage());
            assignment.setStatus(AssignmentStatus.COMPLETED);
            if (request.getNotes() != null) {
                // Припускаємо, що у VehicleAssignment є поле notes. Якщо ні, ігноруємо.
                // assignment.setNotes(request.getNotes());
            }

            // 5. Оновлюємо машину
            vehicle.setMileage(request.getReturnMileage());
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);

            log.info("Рейс для машини ID={} успішно завершено", vehicleId);

            return assignmentMapper.toResponseDTO(assignmentRepository.save(assignment));
        } finally {
            // Очищаємо MDC після обробки запиту
            MDC.remove("operation");
            MDC.remove("vehicleId");
        }
    }

    // ... інші методи без змін ...

    @Transactional(readOnly = true)
    public List<VehicleAssignmentResponseDTO> getVehicleHistory(Long vehicleId) {
        return assignmentRepository.findByVehicleIdOrderByStartTimeDesc(vehicleId).stream()
                .map(assignmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer calculateTotalDistance(LocalDateTime start, LocalDateTime end) {
        Integer total = assignmentRepository.calculateTotalDistance(start, end);
        return total != null ? total : 0;
    }
}