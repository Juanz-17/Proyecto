package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.model.Service;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class PlaceCreateRequest {
    @NotBlank(message = "El título es requerido")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String title;

    @NotBlank(message = "La descripción es requerida")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String description;

    @NotNull(message = "La dirección es requerida")
    private AddressRequest address;

    @NotNull(message = "El precio por noche es requerido")
    @Positive(message = "El precio por noche debe ser mayor a 0")
    private Double nightlyPrice;

    @NotNull(message = "El número máximo de huéspedes es requerido")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    @Max(value = 50, message = "No puede haber más de 50 huéspedes")
    private Integer maxGuests;

    @NotEmpty(message = "Se requiere al menos una imagen")
    @Size(max = 10, message = "No se pueden agregar más de 10 imágenes")
    private List<String> images;

    private List<Service> services;
}