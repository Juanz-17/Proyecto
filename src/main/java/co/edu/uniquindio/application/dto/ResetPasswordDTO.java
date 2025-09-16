package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ResetPasswordDTO(
        @NotBlank String userId,
        @NotBlank @Length(min = 7, max = 20) String newPassword
) {
}
