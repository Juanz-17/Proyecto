package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateCommentDTO(
        @NotBlank String userId,
        @NotBlank String accommodationId,
        @NotBlank @Length(max = 500) String content,
        @NotNull Integer rating // Escala 1-5
) {
}
