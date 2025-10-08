package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.PasswordResetCode;
import co.edu.uniquindio.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    // Buscar código por valor
    Optional<PasswordResetCode> findByCode(String code);

    // Buscar código activo por usuario
    @Query("SELECT prc FROM PasswordResetCode prc WHERE prc.user = :user AND prc.createdAt >= :expirationTime")
    Optional<PasswordResetCode> findValidCodeByUser(
            @Param("user") User user,
            @Param("expirationTime") LocalDateTime expirationTime);

    // Eliminar códigos expirados
    @Modifying
    @Query("DELETE FROM PasswordResetCode prc WHERE prc.createdAt < :expirationTime")
    void deleteExpiredCodes(@Param("expirationTime") LocalDateTime expirationTime);

    // Eliminar todos los códigos de un usuario
    void deleteByUser(User user);
}
