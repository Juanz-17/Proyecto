package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.BookingRequest;
import co.edu.uniquindio.application.dto.ApiResponse;
import co.edu.uniquindio.application.dto.BookingResponse;
import co.edu.uniquindio.application.mappers.BookingMapper;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.BookingStatus;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.services.BookingService;
import co.edu.uniquindio.application.services.PlaceService;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final PlaceService placeService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestParam Long guestId) {

        User guest = userService.getUserById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado"));

        var place = placeService.getPlaceById(request.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        Booking booking = bookingMapper.toEntity(request);
        booking.setGuest(guest);
        booking.setPlace(place);

        Booking createdBooking = bookingService.createBooking(booking);
        BookingResponse response = bookingMapper.toResponse(createdBooking);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Reserva creada exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        BookingResponse response = bookingMapper.toResponse(booking);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/guest/{guestId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByGuest(@PathVariable Long guestId) {
        User guest = userService.getUserById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado"));

        List<Booking> bookings = bookingService.getBookingsByGuest(guest);
        List<BookingResponse> responses = bookings.stream()
                .map(bookingMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByHost(@PathVariable Long hostId) {
        User host = userService.getUserById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        List<Booking> bookings = bookingService.getBookingsByHost(host);
        List<BookingResponse> responses = bookings.stream()
                .map(bookingMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {

        Booking booking = bookingService.updateBookingStatus(id, status);
        BookingResponse response = bookingMapper.toResponse(booking);

        return ResponseEntity.ok(ApiResponse.success(response, "Estado de reserva actualizado exitosamente"));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id,
            @RequestParam String reason) {

        bookingService.cancelBooking(id, reason);
        Booking booking = bookingService.updateBookingStatus(id, BookingStatus.CANCELLED);
        BookingResponse response = bookingMapper.toResponse(booking);

        return ResponseEntity.ok(ApiResponse.success(response, "Reserva cancelada exitosamente"));
    }

    @GetMapping("/metrics/host/{hostId}")
    public ResponseEntity<ApiResponse<HostMetricsResponse>> getHostMetrics(
            @PathVariable Long hostId,
            @RequestParam BookingStatus status) {

        User host = userService.getUserById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        long bookingCount = bookingService.getBookingCountByHostAndStatus(host, status);

        // Crear un DTO específico para las métricas
        HostMetricsResponse metrics = new HostMetricsResponse(hostId, status, bookingCount);

        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @Data
    @AllArgsConstructor
    private static class HostMetricsResponse {
        private Long hostId;
        private BookingStatus status;
        private long bookingCount;
    }
}
