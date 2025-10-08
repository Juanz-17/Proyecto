package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.HostProfile;
import co.edu.uniquindio.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostProfileRepository extends JpaRepository<HostProfile, Long> {

    // Buscar perfil de host por usuario
    Optional<HostProfile> findByUser(User user);

    // Buscar perfil de host por ID de usuario
    Optional<HostProfile> findByUserId(Long userId);

    // Verificar si un usuario tiene perfil de host
    boolean existsByUser(User user);
}

