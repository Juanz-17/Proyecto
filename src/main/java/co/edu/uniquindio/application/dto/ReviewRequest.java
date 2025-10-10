package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "El ID del alojamiento es requerido")
    private Long placeId;

    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;

    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    private String comment;
}
