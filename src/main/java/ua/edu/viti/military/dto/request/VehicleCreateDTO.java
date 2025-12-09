package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.FuelType;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.validation.OnCreate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для реєстрації нового транспортного засобу")
public class VehicleCreateDTO {

    @Schema(description = "Модель техніки", example = "КрАЗ-6322", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = OnCreate.class, message = "Модель обов'язкова")
    @Size(min = 2, max = 100, groups = OnCreate.class, message = "Довжина назви моделі має бути від 2 до 100 символів")
    private String model;

    @Schema(description = "Державний номерний знак", example = "АА1234ВВ", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = OnCreate.class, message = "Номерний знак обов'язковий")
    @Size(min = 4, max = 20, groups = OnCreate.class, message = "Номерний знак повинен містити від 4 до 20 символів")
    // Тут можна додати @Pattern для перевірки формату номера, якщо потрібно
    private String registrationNumber;

    @Schema(description = "Номер двигуна", example = "YAMZ-238-76543", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = OnCreate.class, message = "Номер двигуна обов'язковий")
    @Size(max = 50, groups = OnCreate.class)
    private String engineNumber;

    @Schema(description = "Номер шасі (VIN)", example = "X1234567890")
    @Size(max = 50, groups = OnCreate.class)
    private String chassisNumber;

    @Schema(description = "Поточний пробіг (км)", example = "45000", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = OnCreate.class, message = "Пробіг обов'язковий")
    @PositiveOrZero(groups = OnCreate.class, message = "Пробіг не може бути від'ємним")
    private Integer mileage;

    @Schema(description = "Тип палива", example = "DIESEL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = OnCreate.class, message = "Тип палива обов'язковий")
    private FuelType fuelType;

    @Schema(description = "Поточний статус", example = "OPERATIONAL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = OnCreate.class, message = "Статус техніки обов'язковий")
    private VehicleStatus status;

    @Schema(description = "Інтервал між ТО (км)", example = "10000", defaultValue = "10000")
    @Positive(groups = OnCreate.class, message = "Інтервал ТО має бути більше 0")
    private Integer maintenanceIntervalKm = 10000;

    @Schema(description = "Дата останнього ТО", example = "2023-11-01")
    @PastOrPresent(groups = OnCreate.class, message = "Дата останнього ТО не може бути в майбутньому")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "Пробіг на момент останнього ТО", example = "40000")
    @PositiveOrZero(groups = OnCreate.class, message = "Пробіг на момент ТО не може бути від'ємним")
    private Integer lastMaintenanceMileage;

    @Schema(description = "ID категорії техніки", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = OnCreate.class, message = "Категорія обов'язкова")
    @Positive(groups = OnCreate.class)
    private Long categoryId;

    @Schema(description = "ID закріпленого водія (опційно)", example = "5")
    @Positive(groups = OnCreate.class)
    private Long driverId;
}