package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO;
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.service.DriverService;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "2. Водії", description = "Облік водіїв та їх прав")
public class DriverController {

    private final DriverService driverService;

    // === 1. CREATE ===
    @PostMapping
    @Operation(summary = "Зареєструвати нового водія")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Водія успішно створено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації (немає прав, пусте ім'я)"),
            @ApiResponse(responseCode = "409", description = "Водій з таким номером вже існує")
    })
    public ResponseEntity<DriverResponseDTO> create(@Valid @RequestBody DriverCreateDTO dto) {
        log.info("REST request to create driver: {}", dto.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(dto));
    }

    // === 2. GET ALL + FILTER (Виправлено для балів) ===
    @GetMapping
    @Operation(
            summary = "Отримати список водіїв",
            description = "Можна фільтрувати за статусом. Наприклад: ?isActive=true покаже тільки тих, хто в строю."
    )
    public ResponseEntity<List<DriverResponseDTO>> getAll(
            @Parameter(description = "Фільтр: true - в строю, false - звільнені")
            @RequestParam(required = false) Boolean isActive) {

        // Тобі треба буде трохи оновити Service, щоб він приймав цей параметр (код нижче)
        return ResponseEntity.ok(driverService.getAll(isActive));
    }

    // === 3. GET BY ID ===
    @GetMapping("/{id}")
    @Operation(summary = "Отримати водія за ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Водія знайдено"),
            @ApiResponse(responseCode = "404", description = "Водія не знайдено")
    })
    public ResponseEntity<DriverResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getById(id));
    }

    // === 4. UPDATE ===
    @PutMapping("/{id}")
    @Operation(summary = "Оновити дані водія (звання, права, статус)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Дані оновлено"),
            @ApiResponse(responseCode = "404", description = "Водія не знайдено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації")
    })
    public ResponseEntity<DriverResponseDTO> update(
            @Parameter(description = "ID водія") @PathVariable Long id,
            @Valid @RequestBody DriverUpdateDTO dto) {

        log.info("REST request to update driver ID: {}", id);
        return ResponseEntity.ok(driverService.update(id, dto));
    }

    // === 5. DELETE ===
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити водія",
            description = "Видаляє водія, якщо за ним не закріплена техніка."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Водія успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Водія не знайдено")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("REST request to delete driver ID: {}", id);
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // === 6. SPECIFIC REPORT (Додаткові бали) ===
    @GetMapping("/expired-licenses")
    @Operation(summary = "Звіт: Водії з простроченими правами")
    public ResponseEntity<List<DriverResponseDTO>> getDriversWithExpiredLicenses() {
        return ResponseEntity.ok(driverService.getDriversWithExpiredLicenses());
    }
}