package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Головний метод для Security: пошук юзера для перевірки пароля
    // Використовує граф сутностей, щоб одразу завантажити ролі (оптимізація)
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    // Для валідації при реєстрації (чи зайнятий логін)
    boolean existsByUsername(String username);
}