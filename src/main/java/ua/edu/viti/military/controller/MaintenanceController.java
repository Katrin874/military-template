package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.MaintenanceRequestDTO;
import ua.edu.viti.military.dto.request.MaintenanceCompleteRequestDTO; // <-- ДОДАНО!
import ua.edu.viti.military.dto.response.MaintenanceResponseDTO;
import ua.edu.viti.military.service.MaintenanceService;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Tag(name = "5. Технічне обслуговування", description = "Ремонт та огляд техніки")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    // === 1. СТВОРЕННЯ/ПОЧАТОК ТО ===
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')") // Тільки керівництво може відправляти на ремонт
    @Operation(summary = "Зареєструвати технічне обслуговування (Початок ТО)")
    public ResponseEntity<MaintenanceResponseDTO> create(@Valid @RequestBody MaintenanceRequestDTO dto) {
        return ResponseEntity.ok(maintenanceService.createMaintenance(dto));
    }

    // === 2. ЗАВЕРШЕННЯ ТО (НОВИЙ ЕНДПОІНТ) ===
    @PostMapping("/complete/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')") // Тільки керівництво може завершувати ТО
    @Operation(summary = "Завершити технічне обслуговування. Змінює статус машини на AVAILABLE.")
    public ResponseEntity<Void> completeMaintenance(
            @PathVariable Long vehicleId,
            @Valid @RequestBody MaintenanceCompleteRequestDTO dto) { // <-- ДОДАНО DTO!

        maintenanceService.completeMaintenance(vehicleId, dto); // <-- Передаємо DTO в сервіс
        // Повертаємо 200 OK без тіла (VOID)
        return ResponseEntity.ok().build();
    }

    // === 3. ІСТОРІЯ ===
    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'SOLDIER')") // Всі можуть дивитися історію
    @Operation(summary = "Історія ремонтів машини")
    public ResponseEntity<List<MaintenanceResponseDTO>> getHistory(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(maintenanceService.getHistoryByVehicle(vehicleId));
    }
}