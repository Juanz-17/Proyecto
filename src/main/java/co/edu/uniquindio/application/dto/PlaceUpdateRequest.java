package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.model.Service;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class PlaceUpdateRequest {
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String title;

    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String description;

    private AddressRequest address;

    @Positive(message = "El precio por noche debe ser mayor a 0")
    private Double nightlyPrice;

    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    @Max(value = 50, message = "No puede haber más de 50 huéspedes")
    private Integer maxGuests;

    @Size(max = 10, message = "No se pueden agregar más de 10 imágenes")
    private List<String> images;

    private List<Service> services;
}