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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.VehicleCreateDTO;
import ua.edu.viti.military.dto.request.VehicleUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleResponseDTO;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.service.VehicleService;
import ua.edu.viti.military.validation.OnCreate;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "3. Транспорт", description = "Управління військовою технікою")
public class VehicleController {

    private final VehicleService vehicleService;

    // === 1. CREATE ===
    @PostMapping
    @Operation(
            summary = "Поставити техніку на облік",
            description = """
                    Реєструє нову одиницю техніки в системі.
                    
                    **Бізнес-правила:**
                    - Номерний знак має бути унікальним.
                    - Номер двигуна має бути унікальним.
                    - Категорія техніки повинна існувати в базі.
                    - Якщо вказано водія, він повинен існувати.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Техніку успішно поставлено на облік"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації (пусті поля, некоректні дані)"),
            @ApiResponse(responseCode = "404", description = "Вказану категорію або водія не знайдено"),
            @ApiResponse(responseCode = "409", description = "Машина з таким номером або двигуном вже існує")
    })
    public ResponseEntity<VehicleResponseDTO> create(
            @Validated(OnCreate.class) @RequestBody VehicleCreateDTO dto) {

        log.info("REST request to create vehicle: {}", dto.getRegistrationNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(dto));
    }

    // === 2. GET ALL + FILTER ===
    @GetMapping
    @Operation(
            summary = "Отримати список техніки",
            description = "Повертає список всіх машин. Можна фільтрувати за статусом (наприклад, ?status=OPERATIONAL)."
    )
    @ApiResponse(responseCode = "200", description = "Список успішно отримано")
    public ResponseEntity<List<VehicleResponseDTO>> getAll(
            @Parameter(description = "Фільтр за статусом (необов'язково)")
            @RequestParam(required = false) VehicleStatus status) {

        log.info("REST request to get all vehicles. Filter status: {}", status);
        return ResponseEntity.ok(vehicleService.getAll(status));
    }

    // === 3. GET BY ID ===
    @GetMapping("/{id}")
    @Operation(summary = "Отримати техніку за ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Техніку знайдено"),
            @ApiResponse(responseCode = "404", description = "Техніку з таким ID не знайдено")
    })
    public ResponseEntity<VehicleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    // === 4. UPDATE ===
    @PutMapping("/{id}")
    @Operation(
            summary = "Оновити дані техніки",
            description = "Оновлення пробігу, статусу або зміна закріпленого водія."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Дані оновлено успішно"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації даних"),
            @ApiResponse(responseCode = "404", description = "Техніку або нового водія не знайдено")
    })
    public ResponseEntity<VehicleResponseDTO> update(
            @Parameter(description = "ID техніки") @PathVariable Long id,
            @Valid @RequestBody VehicleUpdateDTO dto) {

        log.info("REST request to update vehicle ID: {}", id);
        return ResponseEntity.ok(vehicleService.update(id, dto));
    }

    // === 5. DELETE ===
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Списати (видалити) техніку",
            description = "Видаляє запис про техніку з бази даних. Дія незворотна."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Техніку успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Техніку з таким ID не знайдено")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID техніки") @PathVariable Long id) {

        log.info("REST request to delete vehicle ID: {}", id);
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // === 6. SPECIFIC ENDPOINT (Business Logic) ===
    @GetMapping("/requiring-maintenance")
    @Operation(
            summary = "Звіт: Техніка, що потребує ТО",
            description = "Повертає список машин, у яких (поточний пробіг - пробіг останнього ТО) > інтервалу обслуговування."
    )
    @ApiResponse(responseCode = "200", description = "Список отримано успішно")
    public ResponseEntity<List<VehicleResponseDTO>> getVehiclesRequiringMaintenance() {
        log.info("REST request to get vehicles requiring maintenance");
        return ResponseEntity.ok(vehicleService.getVehiclesRequiringMaintenance());
    }
}