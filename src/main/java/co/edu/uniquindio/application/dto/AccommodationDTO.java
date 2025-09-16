package co.edu.uniquindio.application.dto;

import java.util.List;

public record AccommodationDTO(
        String id,
        String name,
        String address,
        String description,
        Double pricePerNight,
        Integer capacity,
        List<String> photos,
        String ownerId
) {
}
