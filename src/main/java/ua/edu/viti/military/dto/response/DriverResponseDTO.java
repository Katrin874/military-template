package ua.edu.viti.military.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Публічні дані водія (відповідь сервера)")
public class DriverResponseDTO {

    @Schema(description = "Унікальний ID", example = "10")
    private Long id;

    @Schema(description = "Військовий квиток", example = "AB-123456")
    private String militaryId;

    @Schema(description = "Ім'я", example = "Олексій")
    private String firstName;

    @Schema(description = "Прізвище", example = "Коваленко")
    private String lastName;

    // 🔥 ДОДАНО (Було пропущено)
    @Schema(description = "По-батькові", example = "Іванович")
    private String middleName;

    @Schema(description = "Військове звання", example = "Сержант")
    private String rank;

    @Schema(description = "Номер водійського посвідчення", example = "BX123456")
    private String licenseNumber;

    @Schema(description = "Відкриті категорії", example = "B, C, CE")
    private String licenseCategories;

    @Schema(description = "Дата закінчення дії прав", example = "2030-05-20")
    private LocalDate licenseExpiryDate;

    @Schema(description = "Статус (true = в строю, false = звільнений)", example = "true")
    private Boolean isActive;

    @Schema(description = "Дата реєстрації в системі")
    private LocalDateTime createdAt;
}