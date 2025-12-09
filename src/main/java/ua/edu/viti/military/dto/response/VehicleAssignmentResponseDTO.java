package ua.edu.viti.military.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VehicleAssignmentResponseDTO {
    private Long id;
    private String driverName;
    private String vehicleModel;
    private String registrationNumber;
    private LocalDateTime startDate;
    private boolean isActive;
}