package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.VehicleStatus;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для оновлення стану техніки (передавати тільки змінені поля)")
public class VehicleUpdateDTO {

    @Schema(description = "Новий пробіг (не може бути меншим за поточний)", example = "45500")
    @PositiveOrZero(message = "Пробіг має бути додатнім числом")
    private Integer mileage;

    @Schema(description = "Новий статус техніки", example = "UNDER_MAINTENANCE")
    private VehicleStatus status;

    @Schema(description = "Дата проведення ТО", example = "2023-12-01")
    @PastOrPresent(message = "Дата ТО не може бути в майбутньому")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "Пробіг на момент проведення ТО", example = "45500")
    @PositiveOrZero(message = "Пробіг на момент ТО має бути додатнім")
    private Integer lastMaintenanceMileage;

    @Schema(description = "ID нового водія (для перезакріплення)", example = "12")
    @Positive(message = "ID водія має бути додатнім")
    private Long driverId;
}