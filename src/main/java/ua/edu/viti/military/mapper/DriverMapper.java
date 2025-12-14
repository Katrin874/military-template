package ua.edu.viti.military.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO;
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.entity.Driver;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    // Entity -> Response DTO
    // Тут ми мапимо всі поля, включно з допоміжним методом getIsActive()
    @Mapping(source = "category", target = "licenseCategories")
    @Mapping(source = "isActive", target = "isActive") // Використовує Driver.getIsActive()
    DriverResponseDTO toResponseDTO(Driver entity);

    // List<Entity> -> List<Response DTO>
    List<DriverResponseDTO> toResponseDTOList(List<Driver> entities);

    // Create DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "licenseCategories")
    @Mapping(target = "status", expression = "java(\"ACTIVE\")") // Статус за замовчуванням
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Driver toEntity(DriverCreateDTO dto);

    // Update DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "licenseNumber", ignore = true) // Номер прав не змінюємо
    @Mapping(target = "category", source = "licenseCategories")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(DriverUpdateDTO dto, @MappingTarget Driver entity);
}