package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для створення/оновлення категорії техніки")
public class VehicleCategoryCreateDTO {

    @NotBlank(message = "Назва категорії обов'язкова")
    @Size(max = 100, message = "Назва не може перевищувати 100 символів")
    @Schema(description = "Назва категорії", example = "Вантажний автомобіль (КрАЗ, МАЗ)")
    private String name;

    @NotBlank(message = "Код категорії обов'язковий")
    @Size(max = 50, message = "Код не може перевищувати 50 символів")
    @Schema(description = "Унікальний код/шифр", example = "TRUCK-HVY")
    private String code;

    @Size(max = 500, message = "Опис занадто довгий (максимум 500 символів)")
    @Schema(description = "Детальний опис", example = "Транспорт для перевезення особового складу та вантажів понад 5 тон")
    private String description;

    @NotBlank(message = "Вкажіть необхідну категорію прав")
    @Size(max = 20, message = "Поле категорії прав занадто довге")
    @Schema(description = "Необхідна категорія водійських прав", example = "C")
    private String requiredLicense; // Наприклад: "C"

    @NotNull(message = "Вкажіть вантажопідйомність")
    @Positive(message = "Вантажопідйомність має бути більша за 0")
    @Schema(description = "Максимальна вантажопідйомність (кг)", example = "12000")
    private Integer maxLoadCapacity;
}