package ua.edu.viti.military.dto.response;

import lombok.Builder;
import lombok.Data;
import ua.edu.viti.military.entity.MaintenanceType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MaintenanceResponseDTO {
    private Long id;
    private String vehicleLicensePlate;
    private MaintenanceType type;
    private String description;
    private BigDecimal cost;
    private LocalDate date;
}