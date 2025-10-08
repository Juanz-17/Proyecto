package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Role;
import co.edu.uniquindio.application.model.Status;
import co.edu.uniquindio.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por email
    Optional<User> findByEmail(String email);

    // Verificar si existe un usuario con ese email
    boolean existsByEmail(String email);

    // Buscar usuarios por rol
    List<User> findByRole(Role role);

    // Buscar usuarios por estado
    List<User> findByStatus(Status status);

    // Buscar hosts activos
    List<User> findByRoleAndStatus(Role role, Status status);

    // Buscar usuario por email y estado
    Optional<User> findByEmailAndStatus(String email, Status status);

    // Contar usuarios por rol
    long countByRole(Role role);
}
