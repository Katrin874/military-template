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
import ua.edu.viti.military.dto.request.VehicleCategoryCreateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.service.VehicleCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "1. Категорії техніки", description = "Управління довідником категорій (A, B, C, Танки, БТР)")
public class VehicleCategoryController {

    private final VehicleCategoryService categoryService;

    // === CREATE ===
    @PostMapping
    @Operation(summary = "Створити нову категорію")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Категорію створено успішно"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації"),
            @ApiResponse(responseCode = "409", description = "Категорія з такою назвою вже існує")
    })
    public ResponseEntity<VehicleCategoryResponseDTO> create(@Valid @RequestBody VehicleCategoryCreateDTO dto) {
        log.info("REST request to create category: {}", dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(dto));
    }

    // === READ ALL ===
    @GetMapping
    @Operation(summary = "Отримати всі категорії")
    public ResponseEntity<List<VehicleCategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    // === READ ONE ===
    @GetMapping("/{id}")
    @Operation(summary = "Отримати категорію за ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категорію знайдено"),
            @ApiResponse(responseCode = "404", description = "Категорію не знайдено")
    })
    public ResponseEntity<VehicleCategoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    // === UPDATE (ЦЕ БУЛО ПРОПУЩЕНО) ===
    @PutMapping("/{id}")
    @Operation(summary = "Оновити назву або опис категорії")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категорію оновлено"),
            @ApiResponse(responseCode = "404", description = "Категорію не знайдено")
    })
    public ResponseEntity<VehicleCategoryResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleCategoryCreateDTO dto) { // Використовуємо той самий DTO, бо поля ті ж самі
        log.info("REST request to update category ID: {}", id);
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    // === DELETE ===
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити категорію",
            description = "Видаляє категорію. УВАГА: Якщо до категорії прив'язана техніка, видалення буде заблоковано."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Категорію успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Категорію не знайдено"),
            @ApiResponse(responseCode = "500", description = "Неможливо видалити: категорія використовується")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID категорії") @PathVariable Long id) {

        log.info("REST request to delete category ID: {}", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}