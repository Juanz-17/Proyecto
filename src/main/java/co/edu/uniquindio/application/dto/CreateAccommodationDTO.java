package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CreateAccommodationDTO(
        @NotBlank @Length(max = 100) String name,
        @NotBlank @Length(max = 300) String address,
        @NotBlank String description,
        @NotNull Double pricePerNight,
        @NotNull Integer capacity,
        List<String> photos,
        @NotBlank String hostId
) {
}
