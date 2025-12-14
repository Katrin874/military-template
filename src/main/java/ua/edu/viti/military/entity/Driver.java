package ua.edu.viti.military.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "military_id", unique = true) // Нове поле
    private String militaryId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name") // Нове поле
    private String middleName;

    @Column(nullable = false)
    private String rank;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(nullable = false)
    private String category;

    @Column(name = "license_categories") // Нове поле
    private String licenseCategories;

    @Column(name = "license_expiry_date") // 🔥 КРИТИЧНО ВАЖЛИВЕ ПОЛЕ ДЛЯ ЗВІТУ
    private LocalDate licenseExpiryDate;

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean getIsActive() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    /**
     * Helper-метод для отримання повного імені водія.
     */
    public String getFullName() {
        // Форматуємо: Прізвище Ім'я По батькові
        return String.format("%s %s %s",
                this.lastName,
                this.firstName,
                (this.middleName != null ? this.middleName : "")
        ).trim();
    }
}