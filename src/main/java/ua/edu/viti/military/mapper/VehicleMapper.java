package ua.edu.viti.military.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.viti.military.dto.request.VehicleCreateDTO;
import ua.edu.viti.military.dto.request.VehicleUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleResponseDTO;
import ua.edu.viti.military.entity.Vehicle;

/**
 * MapStruct маппер для перетворення об'єктів Vehicle.
 * Використовує componentModel="spring" для автоматичної інтеграції в контекст Spring.
 */
@Mapper(componentModel = "spring")
public interface VehicleMapper {

    /**
     * Конвертує VehicleCreateDTO (запит на створення) в сутність Vehicle.
     * Встановлює ID зв'язків (Category та Driver).
     */
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "driver.id", source = "driverId")
    Vehicle toEntity(VehicleCreateDTO dto);

    /**
     * Конвертує сутність Vehicle в VehicleResponseDTO (відповідь API).
     * Витягує ID та ім'я з об'єктів-зв'язків.
     */
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "categoryName", source = "category.name")
    VehicleResponseDTO toDto(Vehicle vehicle);

    /**
     * Оновлює існуючу сутність Vehicle даними з VehicleUpdateDTO.
     * Поля з NULL у DTO ігноруються (це поведінка MapStruct за замовчуванням).
     */
    @Mapping(target = "driver.id", source = "driverId") // Оновлення водія через ID
    @Mapping(target = "id", ignore = true) // Ніколи не оновлюємо ID
    @Mapping(target = "createdAt", ignore = true) // ВИПРАВЛЕНО: з created_at на createdAt
    void updateEntity(VehicleUpdateDTO dto, @org.mapstruct.MappingTarget Vehicle vehicle);
}