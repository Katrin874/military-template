package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.viti.military.entity.VehicleAssignment;

import java.util.Optional;

public interface VehicleAssignmentRepository extends JpaRepository<VehicleAssignment, Long> {
    // Знайти активне призначення для конкретної машини (щоб не видати її двічі)
    Optional<VehicleAssignment> findByVehicleIdAndIsActiveTrue(Long vehicleId);

    // Знайти активне призначення для водія (якщо водій може вести тільки 1 авто одночасно)
    Optional<VehicleAssignment> findByDriverIdAndIsActiveTrue(Long driverId);
}