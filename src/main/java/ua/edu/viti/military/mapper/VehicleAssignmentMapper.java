package ua.edu.viti.military.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.viti.military.dto.request.VehicleAssignmentRequestDTO;
import ua.edu.viti.military.dto.response.VehicleAssignmentResponseDTO;
import ua.edu.viti.military.entity.VehicleAssignment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleAssignmentMapper {

    // Маппінг для створення: перетворює ID на Entity-зв'язки
    // Примітка: startTime та startMileage будуть встановлені вручну у Service
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "driver.id", source = "driverId")
    VehicleAssignment toEntity(VehicleAssignmentRequestDTO dto);

    // Маппінг для відповіді DTO (Вирішує LazyInitializationException)
    @Mapping(target = "vehicleNumber", source = "vehicle.registrationNumber")
    @Mapping(target = "driverName", source = "driver.fullName") // Припускаємо, що Driver має метод getFullName()
    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    VehicleAssignmentResponseDTO toResponseDTO(VehicleAssignment entity);

    // Маппінг для списку
    List<VehicleAssignmentResponseDTO> toResponseDTOList(List<VehicleAssignment> entities);

}