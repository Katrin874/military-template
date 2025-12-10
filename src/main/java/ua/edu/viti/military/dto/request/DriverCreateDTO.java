package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateDTO {

    @NotBlank(message = "Військовий квиток обов'язковий")
    @Size(max = 50, message = "Військовий квиток не може бути довшим за 50 символів")
    private String militaryId;

    @NotBlank(message = "Ім'я обов'язкове")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Прізвище обов'язкове")
    @Size(max = 100)
    private String lastName;

    private String middleName;

    private String rank;

    @NotBlank(message = "Номер посвідчення водія обов'язковий")
    @Size(max = 50, message = "Номер посвідчення не може перевищувати 50 символів")
    private String licenseNumber;

    @NotBlank(message = "Категорії прав обов'язкові")
    private String licenseCategories;

    @NotNull(message = "Дата закінчення прав обов'язкова")
    @Future(message = "Термін дії прав має бути в майбутньому")
    private LocalDate licenseExpiryDate;

    @Size(max = 20)
    private String phoneNumber;
}