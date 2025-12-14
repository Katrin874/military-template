package ua.edu.viti.military.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleAssignmentResponseDTO {
    private Long id;
    private String vehicleNumber;
    private String driverName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer startMileage;
    private Integer endMileage;
    private String status;
}