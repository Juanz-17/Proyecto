package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReservationDTO(
        @NotBlank String userId,
        @NotBlank String accommodationId,
        @NotNull @Future LocalDate startDate,
        @NotNull @Future LocalDate endDate,
        @NotNull Integer guests
) {
}
