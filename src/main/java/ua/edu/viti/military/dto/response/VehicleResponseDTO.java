package ua.edu.viti.military.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Детальна інформація про транспортний засіб (відповідь сервера)")
public class VehicleResponseDTO {

    @Schema(description = "Унікальний ID техніки", example = "55")
    private Long id;

    @Schema(description = "Модель", example = "КрАЗ-6322")
    private String model;

    @Schema(description = "Державний номерний знак", example = "АА4567ВВ")
    private String registrationNumber;

    @Schema(description = "Поточний пробіг (км)", example = "45000")
    private Integer mileage;

    @Schema(description = "Тип палива", example = "DIESEL")
    private FuelType fuelType;

    @Schema(description = "Поточний статус", example = "OPERATIONAL")
    private VehicleStatus status;

    // Вкладений об'єкт
    @Schema(description = "Категорія техніки (повна інформація)")
    private VehicleCategoryResponseDTO category;

    // Інформація про водія
    @Schema(description = "ID закріпленого водія (або null)", example = "12")
    private Long driverId;

    @Schema(description = "ПІБ закріпленого водія (або 'Не закріплено')", example = "Коваленко Петро")
    private String driverName;

    // Технічне обслуговування
    @Schema(description = "Інтервал між ТО (км)", example = "10000")
    private Integer maintenanceIntervalKm;

    @Schema(description = "Дата останнього ТО", example = "2023-10-15")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "Пробіг на момент останнього ТО", example = "40000")
    private Integer lastMaintenanceMileage;

    @Schema(description = "Дата взяття на облік")
    private LocalDateTime createdAt;

    @Schema(description = "Дата останнього оновлення даних")
    private LocalDateTime updatedAt;
}