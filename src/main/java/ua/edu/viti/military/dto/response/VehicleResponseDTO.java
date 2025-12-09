package ua.edu.viti.military.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.FuelType;
import ua.edu.viti.military.entity.VehicleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    private Long id;
    private String model;
    private String registrationNumber;
    private Integer mileage;
    private FuelType fuelType;
    private VehicleStatus status;

    // Вкладена категорія (повна інформація)
    private VehicleCategoryResponseDTO category;

    // Інформація про водія (спрощена, тільки ID та Прізвище)
    private Long driverId;
    private String driverName; // Наприклад: "Іваненко І.І."

    private Integer maintenanceIntervalKm;
    private LocalDate lastMaintenanceDate;
    private Integer lastMaintenanceMileage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
