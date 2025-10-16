package co.edu.uniquindio.application;

import co.edu.uniquindio.application.dto.BookingRequest;
import co.edu.uniquindio.application.dto.BookingResponse;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private User user;
    private Place place;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Bryan");

        place = new Place();
        place.setId(2L);
        place.setName("Cabaña en el bosque");
        place.setPricePerNight(200.0);

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setPlace(place);
        booking.setStartDate(LocalDate.of(2025, 10, 20));
        booking.setEndDate(LocalDate.of(2025, 10, 23));
        booking.setTotalPrice(600.0);
    }

    @Test
    void createBooking_DebeGuardarReserva() {
        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setPlaceId(2L);
        request.setStartDate(LocalDate.of(2025, 10, 20));
        request.setEndDate(LocalDate.of(2025, 10, 23));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(placeRepository.findById(2L)).thenReturn(Optional.of(place));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals("Cabaña en el bosque", result.getPlaceName());
        assertEquals(600.0, result.getTotalPrice());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getAllBookings_DebeRetornarLista() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getBookingById_DebeRetornarBookingResponse() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponse result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals("Bryan", result.getUserName());
        assertEquals("Cabaña en el bosque", result.getPlaceName());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void deleteBooking_DebeEliminarReserva() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(1L);

        bookingService.deleteBooking(1L);

        verify(bookingRepository, times(1)).deleteById(1L);
    }
}


