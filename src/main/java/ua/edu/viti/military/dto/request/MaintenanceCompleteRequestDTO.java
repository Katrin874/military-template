package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCompleteRequestDTO {

    @Size(max = 500, message = "Нотатки про завершення не можуть перевищувати 500 символів")
    private String completionNotes;

    // Примітка: completionDate не обов'язковий, ми можемо встановити його в сервісі як LocalDate.now()
}