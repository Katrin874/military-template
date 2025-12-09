package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.VehicleStatus;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateDTO {

    // Ми не дозволяємо міняти engineNumber або category через цей DTO, тільки оперативні дані

    @PositiveOrZero
    private Integer mileage;

    private VehicleStatus status;

    private LocalDate lastMaintenanceDate;
    private Integer lastMaintenanceMileage;

    private Long driverId; // Зміна водія
}
