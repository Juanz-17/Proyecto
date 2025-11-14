package co.edu.uniquindio.application.config;

import co.edu.uniquindio.application.model.*;
import co.edu.uniquindio.application.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo cargar datos si no existen usuarios
        if (userRepository.count() == 0) {
            loadTestData();
        }
    }

    private void loadTestData() {
        System.out.println("Cargando datos de prueba...");

        // Crear usuarios de prueba
        User admin = createUser("Admin", "admin@alojamientos.com", "Admin123", Role.ADMIN, false);
        User host1 = createUser("Carlos Anfitrión", "carlos@alojamientos.com", "Host123", Role.HOST, true);
        User guest1 = createUser("Juan Huésped", "juan@alojamientos.com", "Guest123", Role.GUEST, false);

        // Crear alojamientos
        Place place1 = createPlace("Hermosa finca con piscina",
                "Finca amplia con piscina, jardín y vistas espectaculares",
                host1, "Calle 123, Medellín", "Medellín", 150000.0, 8,
                Arrays.asList("https://example.com/finca1.jpg"),
                Arrays.asList(Service.POOL, Service.WIFI));

        // Crear reservas
        Booking booking1 = createBooking(guest1, place1,
                LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(10), 4, 450000.0, BookingStatus.CONFIRMED);

        System.out.println("Datos de prueba cargados exitosamente!");
    }

    private User createUser(String name, String email, String password, Role role, Boolean isHost) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(Status.ACTIVE);
        user.setPhone("+573001234567");
        user.setDateBirth(LocalDate.now().minusYears(25));
        user.setIsHost(isHost);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private Place createPlace(String title, String description, User host, String address, String city,
                              Double price, Integer maxGuests, List<String> images, List<Service> services) {
        Place place = new Place();
        place.setTitle(title);
        place.setDescription(description);
        place.setHost(host);

        Address addr = new Address();
        addr.setAddress(address);
        addr.setCity(city);

        Location location = new Location();
        location.setLatitude(6.2442);
        location.setLongitude(-75.5812);
        addr.setLocation(location);

        place.setAddress(addr);
        place.setNightlyPrice(price);
        place.setMaxGuests(maxGuests);
        place.setImages(images);
        place.setServices(services);
        place.setStatus(Status.ACTIVE);
        place.setCreatedAt(LocalDateTime.now());

        return placeRepository.save(place);
    }

    private Booking createBooking(User guest, Place place, LocalDateTime checkIn, LocalDateTime checkOut,
                                  Integer guests, Double price, BookingStatus status) {
        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setPlace(place);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuestCount(guests);
        booking.setPrice(price);
        booking.setStatus(status);
        booking.setCreatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
}