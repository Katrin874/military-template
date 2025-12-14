package ua.edu.viti.military.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_assignments")
@EntityListeners(AuditingEntityListener.class) // Вмикає аудит (авто-дату)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Зв'язки
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // Дані про час
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // Дані про пробіг (аналог quantity в МТЗ, але тут ми фіксуємо лічильник)
    @Column(name = "start_mileage")
    private Integer startMileage;

    @Column(name = "end_mileage")
    private Integer endMileage;

    // Статус (Enum)
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AssignmentStatus status;

    // === ВИМОГА ЕТАПУ 2: AUDIT ===
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // === ВИМОГА ЕТАПУ 2: OPTIMISTIC LOCKING ===
    @Version
    private Long version;
}