package ua.edu.viti.military.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Робить цей клас глобальним обробником для всіх контролерів
@Slf4j
public class GlobalExceptionHandler {

    // 1. Не знайдено (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {

        log.error("Resource not found: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                "/errors/resource-not-found",
                "Resource Not Found",
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 2. Дублікат (409)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            WebRequest request) {

        log.error("Duplicate resource: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                "/errors/duplicate-resource",
                "Duplicate Resource",
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 3. Помилка бізнес-логіки (400)
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogic(
            BusinessLogicException ex,
            WebRequest request) {

        log.error("Business logic error: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                "/errors/business-logic",
                "Business Logic Violation",
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 4. Помилки валідації @Valid (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        });

        log.error("Validation errors: {}", validationErrors);

        ErrorResponse error = buildErrorResponse(
                "/errors/validation",
                "Validation Failed",
                HttpStatus.BAD_REQUEST,
                "Помилки валідації вхідних даних",
                request
        );
        error.setErrors(validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 5. Всі інші помилки (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse error = buildErrorResponse(
                "/errors/internal",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутрішня помилка сервера",
                request
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Допоміжний метод для зборку відповіді
    private ErrorResponse buildErrorResponse(String type, String title, HttpStatus status, String detail, WebRequest request) {
        ErrorResponse error = new ErrorResponse();
        error.setType(type);
        error.setTitle(title);
        error.setStatus(status.value());
        error.setDetail(detail);
        error.setInstance(request.getDescription(false).replace("uri=", ""));
        error.setTimestamp(LocalDateTime.now());
        return error;
    }
}
