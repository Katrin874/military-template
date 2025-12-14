package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Username не може бути пустим")
    private String username;

    @NotBlank(message = "Пароль не може бути пустим")
    private String password;
}