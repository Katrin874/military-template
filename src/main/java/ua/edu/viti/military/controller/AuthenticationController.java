package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.viti.military.dto.request.LoginRequestDTO;
import ua.edu.viti.military.dto.request.RegisterRequestDTO;
import ua.edu.viti.military.dto.response.JwtResponseDTO;
import ua.edu.viti.military.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "0. Аутентифікація", description = "Вхід та Реєстрація")
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Вхід в систему (Отримати токен)")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Реєстрація нового користувача")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }
}