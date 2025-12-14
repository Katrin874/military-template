package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleReturnRequestDTO {

    @NotNull(message = "Час повернення є обов'язковим")
    private LocalDateTime returnDate;

    @NotNull(message = "Пробіг на повернення є обов'язковим")
    @Min(value = 0, message = "Пробіг не може бути від'ємним")
    private Integer returnMileage;

    private String notes; // Необов'язкове
}