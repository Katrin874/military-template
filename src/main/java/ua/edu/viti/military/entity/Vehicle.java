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
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Змінили ім'я поля на registrationNumber (як очікує Сервіс)
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(name = "year_of_manufacture")
    private Integer year;

    // === Нові поля для ТО ===
    @Column(name = "engine_number")
    private String engineNumber;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Column(name = "maintenance_interval_km")
    private Integer maintenanceIntervalKm;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "last_maintenance_mileage")
    private Integer lastMaintenanceMileage;
    // ========================

    @Column(nullable = false)
    private Integer mileage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private VehicleCategory category;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // Автоматично оновлює дату при зміні запису
    private LocalDateTime updatedAt;
}