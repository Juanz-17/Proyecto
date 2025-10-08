package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.Review;
import co.edu.uniquindio.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Buscar reseñas por usuario
    List<Review> findByUser(User user);

    // Buscar reseñas por alojamiento
    List<Review> findByPlace(Place place);

    // Buscar reseñas por alojamiento ordenadas por fecha
    List<Review> findByPlaceOrderByCreatedAtDesc(Place place);

    // Buscar reseña específica de un usuario para un alojamiento
    Optional<Review> findByUserAndPlace(User user, Place place);

    // Calificación promedio de un alojamiento
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place = :place")
    Optional<Double> findAverageRatingByPlace(@Param("place") Place place);

    // Contar reseñas de un alojamiento
    long countByPlace(Place place);

    // Buscar reseñas por rango de calificación
    List<Review> findByPlaceAndRatingBetween(Place place, Integer minRating, Integer maxRating);

    // Buscar reseñas con respuestas (para hosts)
    @Query("SELECT r FROM Review r WHERE r.place.host = :host AND r.reply IS NOT NULL")
    List<Review> findReviewsWithRepliesByHost(@Param("host") User host);

    // Buscar reseñas sin respuestas (para hosts)
    @Query("SELECT r FROM Review r WHERE r.place.host = :host AND r.reply IS NULL")
    List<Review> findReviewsWithoutRepliesByHost(@Param("host") User host);

    // Métricas: reseñas por rango de fechas
    List<Review> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place.host = :host " +
            "AND r.createdAt BETWEEN :startDate AND :endDate")
    Optional<Double> findAverageRatingByHostAndDateRange(
            @Param("host") User host,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
