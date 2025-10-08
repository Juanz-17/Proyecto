package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.BookingStatus;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Buscar reservas por huésped
    List<Booking> findByGuest(User guest);

    List<Booking> findByStatus(BookingStatus status);

    // Buscar reservas por huésped y estado
    List<Booking> findByGuestAndStatus(User guest, BookingStatus status);

    // Buscar reservas por alojamiento
    List<Booking> findByPlace(Place place);

    // Buscar reservas por alojamiento y estado
    List<Booking> findByPlaceAndStatus(Place place, BookingStatus status);

    // Buscar reservas por host (a través del place)
    @Query("SELECT b FROM Booking b WHERE b.place.host = :host")
    List<Booking> findByHost(@Param("host") User host);

    // Buscar reservas por host y estado
    @Query("SELECT b FROM Booking b WHERE b.place.host = :host AND b.status = :status")
    List<Booking> findByHostAndStatus(@Param("host") User host, @Param("status") BookingStatus status);

    // Buscar reservas por rango de fechas
    List<Booking> findByCheckInBetweenOrCheckOutBetween(
            LocalDateTime checkInStart, LocalDateTime checkInEnd,
            LocalDateTime checkOutStart, LocalDateTime checkOutEnd);

    // Verificar si hay reservas conflictivas para un alojamiento
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.place = :place " +
            "AND b.status IN (:activeStatuses) " +
            "AND ((b.checkIn BETWEEN :checkIn AND :checkOut) OR (b.checkOut BETWEEN :checkIn AND :checkOut))")
    boolean existsConflictingBooking(
            @Param("place") Place place,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut,
            @Param("activeStatuses") List<BookingStatus> activeStatuses);

    // Buscar reservas pendientes de expiración
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.createdAt < :expirationTime")
    List<Booking> findExpiredPendingBookings(
            @Param("status") BookingStatus status,
            @Param("expirationTime") LocalDateTime expirationTime);

    // Métricas para host: contar reservas por alojamiento y estado
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.place.host = :host AND b.status = :status")
    long countByHostAndStatus(@Param("host") User host, @Param("status") BookingStatus status);

    // Métricas para host: promedio de calificaciones por rango de fechas
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place.host = :host " +
            "AND r.createdAt BETWEEN :startDate AND :endDate")
    Optional<Double> findAverageRatingByHostAndDateRange(
            @Param("host") User host,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}