package ua.edu.viti.military.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vehicle_categories")
@EntityListeners(AuditingEntityListener.class) // Вмикає авто дати
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 50)
    private String code; // Код категорії

    @Column(length = 500)
    private String description;

    // Специфічні поля Варіанту B
    @Column(length = 20)
    private String requiredLicense; // Необхідна категорія прав (напр. "C")

    private Integer maxLoadCapacity;

    // Зв'язок: Одна категорія -> Багато машин
    @OneToMany(mappedBy = "category")
    private List<Vehicle> vehicles;

    // Автоматичні дати
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
