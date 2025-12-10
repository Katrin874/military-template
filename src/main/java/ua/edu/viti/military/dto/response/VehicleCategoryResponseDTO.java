package ua.edu.viti.military.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані категорії техніки (відповідь сервера)")
public class VehicleCategoryResponseDTO {

    @Schema(description = "Унікальний ID", example = "1")
    private Long id;

    @Schema(description = "Назва категорії", example = "Вантажний автомобіль")
    private String name;

    @Schema(description = "Унікальний код", example = "TRUCK-HVY")
    private String code;

    @Schema(description = "Опис", example = "Транспорт для перевезення вантажів понад 5 тон")
    private String description;

    @Schema(description = "Необхідна категорія прав", example = "C")
    private String requiredLicense;

    @Schema(description = "Вантажопідйомність (кг)", example = "12000")
    private Integer maxLoadCapacity;

    @Schema(description = "Дата створення запису")
    private LocalDateTime createdAt;

    @Schema(description = "Дата останнього оновлення")
    private LocalDateTime updatedAt;
}