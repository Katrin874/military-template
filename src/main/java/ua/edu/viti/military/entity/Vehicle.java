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

@Entity
@Table(name = "vehicles")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String model; // Напр. КрАЗ-6322

    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber; // Напр. АА1234ВВ

    @Column(unique = true, length = 50)
    private String engineNumber;

    @Column(unique = true, length = 50)
    private String chassisNumber;

    private Integer manufactureYear;

    @Column(nullable = false)
    private Integer mileage; // Пробіг (км)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType; // DIESEL, PETROL...

    private Double fuelConsumption; // л/100км

    private Integer maintenanceIntervalKm; // Інтервал ТО (напр. 10000)

    private LocalDate lastMaintenanceDate;

    private Integer lastMaintenanceMileage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status; // OPERATIONAL, IN_MAINTENANCE...

    // === Зв'язки ===

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private VehicleCategory category;

    @ManyToOne
    @JoinColumn(name = "driver_id") // Може бути null
    private Driver driver;

    // Автоматичні дати
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
