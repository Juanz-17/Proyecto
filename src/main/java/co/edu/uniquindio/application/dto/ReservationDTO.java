package co.edu.uniquindio.application.dto;

import java.time.LocalDate;

public record ReservationDTO(
        String id,
        String userId,
        String accommodationId,
        LocalDate startDate,
        LocalDate endDate,
        Integer guests,
        Double totalPrice,
        String status
) {
}
