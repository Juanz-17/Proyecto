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
        if (userRepository.count() == 0) {
            loadTestData();
        }
    }

    private void loadTestData() {
        System.out.println("Cargando datos de prueba...");

        // --------------------------
        //        USUARIOS
        // --------------------------
        User admin = createUser("Admin", "admin@alojamientos.com", "Admin123", Role.ADMIN, false);

        User host1 = createUser("Carlos Anfitrión", "carlos@alojamientos.com", "Host123", Role.HOST, true);
        User host2 = createUser("María López", "maria@alojamientos.com", "Host456", Role.HOST, true);

        User guest1 = createUser("Juan Huésped", "juan@alojamientos.com", "Guest123", Role.GUEST, false);
        User guest2 = createUser("Laura Pérez", "laura@alojamientos.com", "Guest456", Role.GUEST, false);
        User guest3 = createUser("Andrés Gómez", "andres@alojamientos.com", "Guest789", Role.GUEST, false);

        // --------------------------
        //      ALOJAMIENTOS
        // --------------------------
        Place place1 = createPlace(
                "Hermosa finca con piscina",
                "Finca amplia con piscina y vista a la montaña",
                host1,
                "Calle 123, Medellín", "Medellín",
                150000.0, 8,
                List.of("https://images.unsplash.com/photo-1507089947368-19c1da9775ae"),
                List.of(Service.POOL, Service.WIFI)
        );

        Place place2 = createPlace(
                "Apartamento moderno en el centro",
                "Apartamento nuevo con excelente ubicación y coworking",
                host2,
                "Cra 45 #10-20, Bogotá", "Bogotá",
                200000.0, 4,
                List.of("https://images.unsplash.com/photo-1560448204-e02f11c3d0e2"),
                List.of(Service.WIFI)
        );

        Place place3 = createPlace(
                "Cabaña rústica en Guatapé",
                "Cabaña frente al lago con deck privado",
                host1,
                "Km 5 Via Peñol", "Guatapé",
                180000.0, 6,
                List.of("https://images.unsplash.com/photo-1505692794403-34cb0b2e5e2c"),
                List.of(Service.WIFI, Service.PARKING)
        );

        Place place4 = createPlace(
                "Mini estudio económico",
                "Ideal para estudiantes o viajeros",
                host2,
                "Barrio La Flora", "Cali",
                90000.0, 2,
                List.of("https://images.unsplash.com/photo-1501183638710-841dd1904471"),
                List.of(Service.WIFI)
        );

        Place place5 = createPlace(
                "Penthouse de lujo",
                "Vistas panorámicas y terraza privada",
                host1,
                "Av. Las Palmas", "Medellín",
                450000.0, 5,
                List.of("https://images.unsplash.com/photo-1494526585095-c41746248156"),
                List.of(Service.WIFI, Service.POOL, Service.PARKING)
        );

        Place place6 = createPlace(
                "Casa campestre familiar",
                "Ideal para reuniones o descanso",
                host2,
                "Km 3 Via Armenia", "Armenia",
                250000.0, 10,
                List.of("https://images.unsplash.com/photo-1599423300746-b62533397364"),
                List.of(Service.POOL, Service.PARKING)
        );

        // --------------------------
        //      RESERVAS
        // --------------------------
        createBooking(guest1, place1,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(7),
                4, 300000.0,
                BookingStatus.CONFIRMED);

        createBooking(guest2, place2,
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(13),
                2, 400000.0,
                BookingStatus.PENDING);

        createBooking(guest3, place3,
                LocalDateTime.now().plusDays(15),
                LocalDateTime.now().plusDays(18),
                3, 540000.0,
                BookingStatus.CONFIRMED);

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
