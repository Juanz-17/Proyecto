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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API para gestión de reservas de alojamientos")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final PlaceService placeService;
    private final BookingMapper bookingMapper;

    @PostMapping
    @Operation(
            summary = "Crear nueva reserva",
            description = "Crea una nueva reserva para un alojamiento específico. Requiere ID del huésped y datos de la reserva."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de reserva inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Huésped o alojamiento no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Fechas no disponibles o conflicto con reserva existente")
    })
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
    @Operation(
            summary = "Obtener reserva por ID",
            description = "Recupera los detalles completos de una reserva específica utilizando su ID único."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reserva encontrada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        BookingResponse response = bookingMapper.toResponse(booking);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/guest/{guestId}")
    @Operation(
            summary = "Obtener reservas por huésped",
            description = "Lista todas las reservas realizadas por un huésped específico, ordenadas por fecha de creación."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Huésped no encontrado")
    })
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
    @Operation(
            summary = "Obtener reservas por anfitrión",
            description = "Lista todas las reservas recibidas por un anfitrión específico para sus alojamientos."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Anfitrión no encontrado")
    })
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
    @Operation(
            summary = "Actualizar estado de reserva",
            description = "Actualiza el estado de una reserva (PENDIENTE, CONFIRMADA, RECHAZADA, COMPLETADA, CANCELADA). Solo disponible para anfitriones."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado de reserva actualizado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Estado inválido o no permitido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para modificar esta reserva")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {

        Booking booking = bookingService.updateBookingStatus(id, status);
        BookingResponse response = bookingMapper.toResponse(booking);

        return ResponseEntity.ok(ApiResponse.success(response, "Estado de reserva actualizado exitosamente"));
    }

    @PostMapping("/{id}/cancel")
    @Operation(
            summary = "Cancelar reserva",
            description = "Cancela una reserva existente. Disponible para huéspedes (hasta cierto tiempo antes) y anfitriones bajo ciertas condiciones."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "No se puede cancelar en este estado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para cancelar esta reserva")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id,
            @RequestParam String reason) {

        bookingService.cancelBooking(id, reason);
        Booking booking = bookingService.updateBookingStatus(id, BookingStatus.CANCELLED);
        BookingResponse response = bookingMapper.toResponse(booking);

        return ResponseEntity.ok(ApiResponse.success(response, "Reserva cancelada exitosamente"));
    }

    @GetMapping("/metrics/host/{hostId}")
    @Operation(
            summary = "Obtener métricas de anfitrión",
            description = "Recupera estadísticas y métricas de reservas para un anfitrión específico, filtradas por estado."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Métricas obtenidas exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Anfitrión no encontrado")
    })
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