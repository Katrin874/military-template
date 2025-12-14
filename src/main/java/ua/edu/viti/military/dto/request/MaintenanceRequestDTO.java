package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ua.edu.viti.military.entity.MaintenanceType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MaintenanceRequestDTO {
    @NotNull(message = "ID машини обов'язкове")
    private Long vehicleId;

    @NotNull(message = "Тип робіт обов'язковий")
    private MaintenanceType type;

    @NotBlank(message = "Опис не може бути пустим")
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", message = "Вартість не може бути від'ємною")
    private BigDecimal cost;

    private LocalDate date;
}