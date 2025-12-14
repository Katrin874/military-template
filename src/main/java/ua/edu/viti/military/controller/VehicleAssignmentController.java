package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.VehicleAssignmentRequestDTO;
import ua.edu.viti.military.dto.request.VehicleReturnRequestDTO;
import ua.edu.viti.military.dto.response.VehicleAssignmentResponseDTO;
import ua.edu.viti.military.service.VehicleAssignmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "4. Журнал виїздів (Movement Log)", description = "Управління виїздами, поверненням та статистикою")
public class VehicleAssignmentController {

    private final VehicleAssignmentService assignmentService;

    // === 1. ПОЧАТОК РЕЙСУ (Start Assignment) ===
    @PostMapping("/start")
    @Operation(summary = "Відправити машину в рейс (Транзакція)")
    public ResponseEntity<VehicleAssignmentResponseDTO> startAssignment(
            @Valid @RequestBody VehicleAssignmentRequestDTO dto) {

        log.info("REST request to start assignment for vehicle: {}", dto.getVehicleId());
        VehicleAssignmentResponseDTO response = assignmentService.startAssignment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // === 2. ЗАВЕРШЕННЯ РЕЙСУ (Complete Assignment) ===
    @PostMapping("/complete/{vehicleId}")
    @Operation(summary = "Повернути машину та оновити пробіг (Транзакція)")
    public ResponseEntity<VehicleAssignmentResponseDTO> completeAssignment(
            @PathVariable Long vehicleId,
            @Valid @RequestBody VehicleReturnRequestDTO dto) {

        log.info("REST request to complete assignment for vehicle: {}", vehicleId);
        VehicleAssignmentResponseDTO response = assignmentService.completeAssignment(vehicleId, dto);
        return ResponseEntity.ok(response);
    }

    // === 3. ІСТОРІЯ (History) ===
    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Історія виїздів конкретної машини")
    public ResponseEntity<List<VehicleAssignmentResponseDTO>> getVehicleHistory(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(assignmentService.getVehicleHistory(vehicleId));
    }

    // === 4. СТАТИСТИКА (Statistics) ===
    @GetMapping("/statistics/distance")
    @Operation(summary = "Підрахунок загального пробігу парку за період")
    public ResponseEntity<Integer> getTotalDistance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        Integer totalKm = assignmentService.calculateTotalDistance(start, end);
        return ResponseEntity.ok(totalKm);
    }
}