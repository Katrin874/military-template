package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для оновлення інформації про водія (передавати тільки ті поля, що змінюються)")
public class DriverUpdateDTO {

    @Schema(description = "Військове звання", example = "Сержант")
    @Size(max = 50, message = "Звання не може перевищувати 50 символів")
    private String rank;

    @Schema(description = "Номер посвідчення водія", example = "BX123456")
    @Size(max = 50, message = "Номер посвідчення не може перевищувати 50 символів")
    private String licenseNumber;

    @Schema(description = "Відкриті категорії", example = "B, C1, C")
    @Size(max = 50, message = "Рядок категорій занадто довгий")
    private String licenseCategories;

    @Schema(description = "Дата закінчення дії прав (має бути в майбутньому)", example = "2030-12-31")
    @Future(message = "Нова дата закінчення прав має бути в майбутньому")
    private LocalDate licenseExpiryDate;

    @Schema(description = "Номер телефону", example = "+380971234567")
    @Size(max = 20, message = "Номер телефону занадто довгий")
    private String phoneNumber;

    @Schema(description = "Чи активний водій (false = звільнений/відсторонений)", example = "true")
    private Boolean isActive;
}