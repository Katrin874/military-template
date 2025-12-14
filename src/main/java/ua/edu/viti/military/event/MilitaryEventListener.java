package ua.edu.viti.military.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ua.edu.viti.military.service.MetricsService; // <-- ДОДАНО!

@Component
@RequiredArgsConstructor
@Slf4j
public class MilitaryEventListener {

    private final MetricsService metricsService; // <-- ІНЖЕКЦІЯ METRICS SERVICE

    /**
     * Обробник призначення техніки
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleVehicleAssigned(VehicleAssignedEvent event) {
        log.info("📢 START PROCESSING EVENT: VehicleAssignedEvent (ASYNCHRONOUSLY)");

        log.info("Vehicle {} assigned to driver {} for unit {}",
                event.getVehicleRegistrationNumber(), event.getDriverName(), event.getUnit());

        try {
            // 1. Надсилання Email/Telegram нотифікації (ІМІТАЦІЯ ТРИВАЛОЇ ОПЕРАЦІЇ)
            sendAssignmentNotification(event);

            // 2. Оновлення централізованого логу статистики (імітація)
            log.info("Updating central logistics statistics for assignment...");

            // Примітка: Лічильник успішних призначень (recordAssignmentStarted) викликається в синхронному сервісі.

            log.info("✅ VehicleAssignedEvent processed successfully (took 2 seconds).");

        } catch (Exception e) {
            log.error("❌ Error processing VehicleAssignedEvent", e);
        }
    }

    /**
     * === НОВИЙ ОБРОБНИК: ЗАВЕРШЕННЯ ТЕХНІЧНОГО ОБСЛУГОВУВАННЯ ===
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleMaintenanceCompleted(MaintenanceCompletedEvent event) {
        log.info("🛠️ START PROCESSING EVENT: MaintenanceCompletedEvent (ASYNCHRONOUSLY)");

        log.info("Maintenance ID {} completed. Vehicle {} is READY for service.",
                event.getMaintenanceId(), event.getRegistrationNumber());

        try {
            // 1. Надсилання нотифікації військовим про готовність машини
            sendCompletionNotification(event);

            // 2. Аудит та оновлення статистики
            log.info("Updating vehicle readiness statistics...");

            // ✅ ФІКСУЄМО МЕТРИКУ: Успішне завершення ТО
            metricsService.recordMaintenanceCompleted(); // <-- ДОДАНО!

            log.info("✅ MaintenanceCompletedEvent processed successfully.");

        } catch (Exception e) {
            log.error("❌ Error processing MaintenanceCompletedEvent", e);
        }
    }

    // Helper метод для імітації затримки при призначенні
    private void sendAssignmentNotification(VehicleAssignedEvent event) {
        log.info("Sending notification about vehicle assignment for: {}", event.getVehicleRegistrationNumber());
        try {
            Thread.sleep(2000); // Імітація затримки на 2 секунди
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Helper метод для імітації нотифікації про завершення ТО
    private void sendCompletionNotification(MaintenanceCompletedEvent event) {
        log.info("Sending notification: Vehicle {} is now available after maintenance.", event.getRegistrationNumber());
        try {
            Thread.sleep(100); // Невелика затримка для імітації
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}