package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCategoryCreateDTO {

    @NotBlank(message = "Назва категорії обов'язкова")
    @Size(max = 100, message = "Назва занадто довга")
    private String name;

    @Size(max = 50, message = "Код занадто довгий")
    private String code;

    @Size(max = 500)
    private String description;

    @Size(max = 20)
    private String requiredLicense; // Наприклад: "C"

    @Positive(message = "Вантажопідйомність має бути додатною")
    private Integer maxLoadCapacity;
}
