package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.BookingStatus;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.ReviewRepository;
import co.edu.uniquindio.application.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        // Validaciones básicas
        if (booking.getPlace() == null) {
            throw new IllegalArgumentException("El alojamiento es requerido");
        }

        if (booking.getGuest() == null) {
            throw new IllegalArgumentException("El huésped es requerido");
        }

        if (booking.getCheckIn() == null || booking.getCheckOut() == null) {
            throw new IllegalArgumentException("Las fechas de check-in y check-out son requeridas");
        }

        if (booking.getCheckIn().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de check-in no puede ser en el pasado");
        }

        if (booking.getCheckOut().isBefore(booking.getCheckIn()) ||
                booking.getCheckOut().isEqual(booking.getCheckIn())) {
            throw new IllegalArgumentException("La fecha de check-out debe ser posterior al check-in");
        }

        if (booking.getGuestCount() == null || booking.getGuestCount() <= 0) {
            throw new IllegalArgumentException("El número de huéspedes debe ser mayor a 0");
        }

        Place place = placeRepository.findById(booking.getPlace().getId())
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        // Verificar disponibilidad
        if (!isPlaceAvailable(place, booking.getCheckIn(), booking.getCheckOut())) {
            throw new IllegalArgumentException("El alojamiento no está disponible para las fechas seleccionadas");
        }

        // Verificar capacidad
        if (booking.getGuestCount() > place.getMaxGuests()) {
            throw new IllegalArgumentException("El número de huéspedes excede la capacidad máxima del alojamiento");
        }

        // Calcular precio
        double price = calculateBookingPrice(place, booking.getCheckIn(), booking.getCheckOut(), booking.getGuestCount());
        booking.setPrice(price);

        // Establecer valores por defecto
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getBookingsByGuest(User guest) {
        return bookingRepository.findByGuest(guest);
    }

    @Override
    public List<Booking> getBookingsByHost(User host) {
        return bookingRepository.findByHost(host);
    }

    @Override
    public List<Booking> getBookingsByPlace(Place place) {
        return bookingRepository.findByPlace(place);
    }

    @Override
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Validar transiciones de estado
        if (booking.getStatus() == BookingStatus.CANCELLED && status != BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("No se puede reactivar una reserva cancelada");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED && status != BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("No se puede modificar una reserva completada");
        }

        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Validar que no esté ya cancelada o completada
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("La reserva ya está cancelada");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("No se puede cancelar una reserva completada");
        }

        // Verificar política de cancelación (48 horas antes)
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilCheckIn = ChronoUnit.HOURS.between(now, booking.getCheckIn());

        if (hoursUntilCheckIn < 48) {
            throw new IllegalArgumentException("No se puede cancelar menos de 48 horas antes del check-in");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public boolean isPlaceAvailable(Place place, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<BookingStatus> activeStatuses = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        return !bookingRepository.existsConflictingBooking(place, checkIn, checkOut, activeStatuses);
    }

    @Override
    public double calculateBookingPrice(Place place, LocalDateTime checkIn, LocalDateTime checkOut, Integer guests) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) nights = 1; // Mínimo una noche

        double basePrice = place.getNightlyPrice() * nights;

        // Podrías agregar lógica adicional aquí (impuestos, tarifas, etc.)
        return basePrice;
    }

    @Override
    public List<Booking> getExpiredPendingBookings() {
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(24); // 24 horas para confirmar
        return bookingRepository.findExpiredPendingBookings(BookingStatus.PENDING, expirationTime);
    }

    @Override
    @Transactional
    public void expirePendingBookings() {
        List<Booking> expiredBookings = getExpiredPendingBookings();
        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }
    }

    @Override
    public long getBookingCountByHostAndStatus(User host, BookingStatus status) {
        return bookingRepository.countByHostAndStatus(host, status);
    }

    @Override
    public double getAverageRatingByHostAndDateRange(User host, LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.findAverageRatingByHostAndDateRange(host, startDate, endDate)
                .orElse(0.0);
    }
}
