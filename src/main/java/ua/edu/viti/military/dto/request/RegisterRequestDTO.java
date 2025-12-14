package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class RegisterRequestDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 4, max = 100)
    private String password;

    @NotBlank
    private String fullName;

    private String rank; // Військове звання (Рядовий, Сержант...)

    // Список ролей, які хочемо дати (наприклад: ["ROLE_OFFICER"])
    private Set<String> roles;
}