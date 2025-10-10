package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.Service;
import co.edu.uniquindio.application.model.Status;
import co.edu.uniquindio.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // Buscar alojamientos por host
    List<Place> findByHost(User host);

    // Buscar alojamientos por host y estado
    List<Place> findByHostAndStatus(User host, Status status);

    // Buscar alojamientos por ciudad
    List<Place> findByAddressCityIgnoreCase(String city);

    // Buscar alojamientos por estado
    List<Place> findByStatus(Status status);

    // Buscar alojamientos activos
    List<Place> findByStatusOrderByCreatedAtDesc(Status status);

    // Buscar alojamientos por rango de precio
    List<Place> findByNightlyPriceBetweenAndStatus(Double minPrice, Double maxPrice, Status status);

    // Buscar alojamientos por ciudad y rango de precio
    List<Place> findByAddressCityIgnoreCaseAndNightlyPriceBetweenAndStatus(
            String city, Double minPrice, Double maxPrice, Status status);

    // Buscar alojamientos por servicios - CORREGIDO
    @Query("SELECT DISTINCT p FROM Place p JOIN p.services s WHERE s IN :services AND p.status = :status")
    List<Place> findByServicesInAndStatus(@Param("services") List<Service> services, // ← CAMBIADO A Service
                                          @Param("status") Status status);

    // Buscar alojamientos disponibles (sin reservas conflictivas)
    @Query("SELECT p FROM Place p WHERE p.status = :status " +
            "AND p.address.city LIKE %:city% " +
            "AND p.nightlyPrice BETWEEN :minPrice AND :maxPrice " +
            "AND p.maxGuests >= :guests " +
            "AND p.id NOT IN (" +
            "    SELECT b.place.id FROM Booking b " +
            "    WHERE b.status IN (co.edu.uniquindio.application.model.BookingStatus.CONFIRMED, co.edu.uniquindio.application.model.BookingStatus.PENDING) " +
            "    AND ((b.checkIn BETWEEN :checkIn AND :checkOut) OR (b.checkOut BETWEEN :checkIn AND :checkOut))" +
            ")")
    List<Place> findAvailablePlaces(
            @Param("city") String city,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut,
            @Param("guests") Integer guests,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("status") Status status);

    // Contar alojamientos por host
    long countByHost(User host);

}
