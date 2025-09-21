package co.edu.uniquindio.application.dto;

import java.time.LocalDateTime;

public record CommentDTO(
        String id,
        String userId,
        String accommodationId,
        String content,
        Integer rating,
        LocalDateTime createdAt
) {
}
