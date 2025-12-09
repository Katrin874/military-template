package ua.edu.viti.military.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureDateValidator.class) // <-- Тут може бути червоне, поки не створиш файл нижче
@Documented
public @interface FutureDate {
    String message() default "Дата має бути в майбутньому";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}