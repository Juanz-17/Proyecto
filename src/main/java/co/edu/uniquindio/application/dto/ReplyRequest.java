package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReplyRequest {
    @NotBlank(message = "El mensaje de respuesta es requerido")
    @Size(max = 500, message = "La respuesta no puede exceder 500 caracteres")
    private String message;
}