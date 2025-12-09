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

    @Column(nullable = false, unique = true, length = 50)
    private String militaryId; // Номер військового квитка

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 100)
    private String middleName;

    @Column(length = 50)
    private String rank; // Звання

    @Column(unique = true, length = 50)
    private String licenseNumber; // Номер посвідчення

    @Column(length = 50)
    private String licenseCategories; // Категорії прав (напр. "B,C,CE")

    private LocalDate licenseExpiryDate; // Коли закінчуються права

    @Column(length = 20)
    private String phoneNumber;

    private Boolean isActive = true; // Чи в строю

    // Зв'язок: Водій може мати історію закріплених машин
    @OneToMany(mappedBy = "driver")
    private List<Vehicle> assignedVehicles;

    // Автоматичні дати
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
