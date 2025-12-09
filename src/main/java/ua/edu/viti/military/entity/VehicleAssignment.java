package ua.edu.viti.military.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "vehicle_assignments")
public class VehicleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean isActive; // Прапорець для швидкого пошуку активних призначень


    public VehicleAssignment(Driver driver, Vehicle vehicle) {
        this.driver = driver;
        this.vehicle = vehicle;
        this.startDate = LocalDateTime.now();
        this.isActive = true;
    }

    public VehicleAssignment() {}
}