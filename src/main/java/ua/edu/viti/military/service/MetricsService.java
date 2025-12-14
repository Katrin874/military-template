package ua.edu.viti.military.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // Counters (Лічильники)
    private final Counter vehicleAssignmentsCounter; // Кількість виїздів
    private final Counter maintenanceCompletedCounter; // Кількість завершених ТО
    private final Counter assignmentFailedCounter; // Кількість невдалих виїздів

    // Timers (Вимірювання тривалості)
    private final Timer assignmentOperationTimer; // Час на обробку виїзду

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Ініціалізація metrics
        this.vehicleAssignmentsCounter = Counter.builder("military.vehicle.assignments.total")
                .description("Total number of vehicle assignments started")
                .tag("operation", "start")
                .register(meterRegistry);

        this.maintenanceCompletedCounter = Counter.builder("military.maintenance.completed.total")
                .description("Total number of maintenance tasks completed")
                .tag("operation", "complete_maintenance")
                .register(meterRegistry);

        this.assignmentFailedCounter = Counter.builder("military.assignment.failed.total")
                .description("Total number of failed assignment attempts (e.g., status MAINTENANCE)")
                .tag("reason", "unavailable_vehicle")
                .register(meterRegistry);

        this.assignmentOperationTimer = Timer.builder("military.operations.assignment.duration")
                .description("Time taken to process a successful vehicle assignment")
                .tag("operation", "start_assignment")
                .register(meterRegistry);
    }

    /**
     * Зафіксувати успішний виїзд (викликається в сервісі)
     */
    public void recordAssignmentStarted() {
        vehicleAssignmentsCounter.increment();
    }

    /**
     * Зафіксувати успішне завершення ТО (викликається в Event Listener)
     */
    public void recordMaintenanceCompleted() {
        maintenanceCompletedCounter.increment();
    }

    /**
     * Зафіксувати невдалу спробу виїзду
     */
    public void recordAssignmentFailed() {
        assignmentFailedCounter.increment();
    }

    /**
     * Виміряти тривалість операції з призначення виїзду
     */
    public <T> T measureAssignmentOperation(Supplier<T> operation) {
        return assignmentOperationTimer.record(operation);
    }
}