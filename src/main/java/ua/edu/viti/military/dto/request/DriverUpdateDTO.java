package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Дані для оновлення інформації про водія")
public class DriverUpdateDTO {

    @Schema(description = "Військове звання", example = "Сержант")
    private String rank;

    @Schema(description = "Номер посвідчення водія", example = "BX123456")
    private String licenseNumber;

    @Schema(description = "Відкриті категорії", example = "B, C1, C")
    private String licenseCategories;

    @Schema(description = "Дата закінчення дії прав", example = "2030-12-31")
    private LocalDate licenseExpiryDate;

    @Schema(description = "Номер телефону", example = "+380971234567")
    private String phoneNumber;

    @Schema(description = "Чи активний водій (false = звільнений/відсторонений)", example = "true")
    private Boolean isActive;
}