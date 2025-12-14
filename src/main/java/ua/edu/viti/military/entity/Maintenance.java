package ua.edu.viti.military.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType type;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(nullable = false)
    private LocalDate maintenanceDate;

    // === НОВІ ПОЛЯ ДЛЯ ФІКСАЦІЇ ЗАВЕРШЕННЯ ТО ===

    // Нотатки про завершення (Nullable, оскільки заповнюється пізніше)
    @Column(length = 500)
    private String completionNotes;

    // Дата завершення ТО (Nullable)
    @Column
    private LocalDate completionDate;

    // ===========================================

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}