package ua.edu.viti.military.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "drivers")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Особисті дані ===

    @Column(nullable = false, unique = true, length = 50)
    private String militaryId; // Номер військового квитка (Унікальний)

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 100)
    private String middleName;

    @Column(length = 50)
    private String rank; // Звання

    // === Дані водійського посвідчення (Обов'язкові) ===

    @Column(nullable = false, unique = true, length = 50)
    private String licenseNumber; // Номер посвідчення

    @Column(nullable = false, length = 50)
    private String licenseCategories; // Категорії (напр. "B, C, CE")

    @Column(nullable = false)
    private LocalDate licenseExpiryDate; // Дата закінчення дії прав

    // === Контакти та Статус ===

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean isActive = true; // Статус (для Soft Delete)

    // === Зв'язки ===

    // Один водій може бути закріплений за кількома машинами (історія)
    // fetch = FetchType.LAZY — завантажуємо список тільки коли звертаємось до нього
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Vehicle> assignedVehicles;

    // === Аудит (Системні поля) ===

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}