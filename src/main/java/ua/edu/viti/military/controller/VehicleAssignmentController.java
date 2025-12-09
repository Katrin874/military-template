package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.VehicleAssignmentRequestDTO;
import ua.edu.viti.military.dto.response.VehicleAssignmentResponseDTO;
import ua.edu.viti.military.service.VehicleAssignmentService;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Vehicle Assignment (Stage 2)", description = "Контролер для управління призначенням та поверненням військової техніки")
public class VehicleAssignmentController {

    private final VehicleAssignmentService assignmentService;

    @PostMapping
    @Operation(
            summary = "Призначити техніку водію (Виїзд)",
            description = "Створює запис про видачу авто. Перевіряє наявність прав, статус водія та технічний стан машини (ТО)."
    )
    public ResponseEntity<VehicleAssignmentResponseDTO> createAssignment(@RequestBody VehicleAssignmentRequestDTO request) {
        VehicleAssignmentResponseDTO response = assignmentService.assignVehicle(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/complete")
    @Operation(
            summary = "Завершити рейс (Повернення в парк)",
            description = "Фіксує час повернення авто та оновлює загальний пробіг техніки."
    )
    public ResponseEntity<Void> completeAssignment(
            @Parameter(description = "ID призначення (Assignment ID)")
            @PathVariable Long id,

            @Parameter(description = "Показник одометра при поверненні (км)")
            @RequestParam Integer finalMileage
    ) {
        assignmentService.completeAssignment(id, finalMileage);
        return ResponseEntity.ok().build();
    }
}