package ua.edu.viti.military.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ua.edu.viti.military.entity.Vehicle;
import java.time.LocalDateTime;

@Getter
public class VehicleAssignedEvent extends ApplicationEvent {

    private final Long vehicleId;
    private final String vehicleRegistrationNumber;
    private final String driverName;
    private final String unit;
    private final String performedBy;
    // ВИДАЛЕНО: private final LocalDateTime timestamp; <-- Це викликало конфлікт!

    // Конструктор
    public VehicleAssignedEvent(Object source, Vehicle vehicle, String driverName, String unit, String performedBy) {
        super(source); // <-- Батьківський клас ApplicationEvent автоматично зберігає час (timestamp)
        this.vehicleId = vehicle.getId();
        this.vehicleRegistrationNumber = vehicle.getRegistrationNumber();
        this.driverName = driverName;
        this.unit = unit;
        this.performedBy = performedBy;
        // ВИДАЛЕНО: this.timestamp = LocalDateTime.now();
    }

    // Примітка: Доступ до часу тепер здійснюється через стандартний event.getTimestamp(), який поверне long.
}