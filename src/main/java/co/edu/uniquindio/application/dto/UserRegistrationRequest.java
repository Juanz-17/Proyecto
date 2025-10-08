package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserRegistrationRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número")
    private String password;

    private LocalDateTime dateBirth;
    private String phone;
    private String photoUrl;

    private Boolean isHost = false;
    private String legalDocument; // Solo si se registra como host
    private String aboutMe; // Solo si se registra como host
}