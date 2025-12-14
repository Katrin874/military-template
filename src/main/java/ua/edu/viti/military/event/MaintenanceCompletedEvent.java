package ua.edu.viti.military.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ua.edu.viti.military.entity.Maintenance;
import ua.edu.viti.military.entity.Vehicle;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MaintenanceCompletedEvent extends ApplicationEvent {

    private final Long maintenanceId;
    private final Long vehicleId;
    private final String registrationNumber;

    // Нові поля, які беруться з оновленої сутності Maintenance
    private final String completionNotes;
    private final LocalDate completionDate;

    private final LocalDateTime eventCreatedAt; // Час створення самого Event
    private final String performedBy;

    public MaintenanceCompletedEvent(Object source, Maintenance maintenance, String performedBy) {
        super(source);
        this.maintenanceId = maintenance.getId();
        this.vehicleId = maintenance.getVehicle().getId();
        this.registrationNumber = maintenance.getVehicle().getRegistrationNumber();

        // Поля, які тепер доступні після оновлення сутності
        this.completionNotes = maintenance.getCompletionNotes();
        this.completionDate = maintenance.getCompletionDate();

        this.eventCreatedAt = LocalDateTime.now();
        this.performedBy = performedBy;
    }
}