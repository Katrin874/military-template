package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleAssignmentRequestDTO {

    @NotNull(message = "ID машини є обов'язковим")
    @Positive(message = "ID машини має бути більшим за 0")
    private Long vehicleId;

    @NotNull(message = "ID водія є обов'язковим")
    @Positive(message = "ID водія має бути більшим за 0")
    private Long driverId;

    // ДОДАНО: Поля, які необхідні для логування (були відсутні)
    @NotNull(message = "Час виїзду є обов'язковим")
    @FutureOrPresent(message = "Час виїзду не може бути в минулому")
    private LocalDateTime startTime;

    @NotNull(message = "Пробіг на виїзді є обов'язковим")
    @Min(value = 0, message = "Пробіг не може бути від'ємним")
    private Integer startMileage;

    @NotBlank(message = "Пункт призначення є обов'язковим")
    private String destination;

    @NotBlank(message = "Мета поїздки є обов'язковою")
    private String purpose;
}