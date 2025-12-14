package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.Role;      // <--- ОСЬ ЦЬОГО НЕ ВИСТАЧАЛО
import ua.edu.viti.military.entity.RoleName;  // <--- І ЦЬОГО

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}