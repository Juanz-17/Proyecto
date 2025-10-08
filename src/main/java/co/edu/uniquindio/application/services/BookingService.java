package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.BookingStatus;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking createBooking(Booking booking);
    Optional<Booking> getBookingById(Long id);
    List<Booking> getBookingsByGuest(User guest);
    List<Booking> getBookingsByHost(User host);
    List<Booking> getBookingsByPlace(Place place);
    List<Booking> getBookingsByStatus(BookingStatus status);
    Booking updateBookingStatus(Long id, BookingStatus status);
    void cancelBooking(Long id, String reason);
    boolean isPlaceAvailable(Place place, LocalDateTime checkIn, LocalDateTime checkOut);
    double calculateBookingPrice(Place place, LocalDateTime checkIn, LocalDateTime checkOut, Integer guests);
    List<Booking> getExpiredPendingBookings();
    void expirePendingBookings();

    // Métricas para hosts
    long getBookingCountByHostAndStatus(User host, BookingStatus status);
    double getAverageRatingByHostAndDateRange(User host, LocalDateTime startDate, LocalDateTime endDate);
}
