package co.edu.uniquindio.application.dto;

public record CommentDTO(
        String id,
        String userId,
        String accommodationId,
        String content,
        Integer rating,
        String createdAt
) {
}
