package ua.edu.viti.military.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.edu.viti.military.dto.request.VehicleCategoryCreateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.entity.VehicleCategory;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleCategoryMapper {

    // === Entity -> Response DTO ===
    VehicleCategoryResponseDTO toResponseDTO(VehicleCategory entity);

    // === List<Entity> -> List<Response DTO> ===
    List<VehicleCategoryResponseDTO> toResponseDTOList(List<VehicleCategory> entities);

    // === Create DTO -> Entity ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    VehicleCategory toEntity(VehicleCategoryCreateDTO dto);

    // === Оновлення існуючого Entity з DTO ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(VehicleCategoryCreateDTO dto, @MappingTarget VehicleCategory entity);
}