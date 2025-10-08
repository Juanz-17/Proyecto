package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    @NotNull(message = "El ID del alojamiento es requerido")
    private Long placeId;

    @NotNull(message = "La fecha de check-in es requerida")
    @Future(message = "La fecha de check-in debe ser futura")
    private LocalDateTime checkIn;

    @NotNull(message = "La fecha de check-out es requerida")
    @Future(message = "La fecha de check-out debe ser futura")
    private LocalDateTime checkOut;

    @NotNull(message = "El número de huéspedes es requerido")
    @Positive(message = "Debe haber al menos 1 huésped")
    private Integer guestCount;
}
