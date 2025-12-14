package ua.edu.viti.military.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.viti.military.dto.request.MaintenanceRequestDTO;
import ua.edu.viti.military.dto.response.MaintenanceResponseDTO;
import ua.edu.viti.military.entity.Maintenance;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MaintenanceMapper {

    // 👇 ТУТ БУЛА ПОМИЛКА: vehicle.licensePlate -> vehicle.registrationNumber
    @Mapping(source = "vehicle.registrationNumber", target = "vehicleLicensePlate")
    @Mapping(source = "maintenanceDate", target = "date")
    MaintenanceResponseDTO toResponseDTO(Maintenance entity);

    List<MaintenanceResponseDTO> toResponseDTOList(List<Maintenance> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "date", target = "maintenanceDate")
    Maintenance toEntity(MaintenanceRequestDTO dto);
}