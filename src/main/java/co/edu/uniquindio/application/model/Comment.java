package co.edu.uniquindio.application.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Comment {
    private Long id;
    private User guest;
    private Accommodation accommodation;
    private int rating; // 1 a 5
    private String text;
    private LocalDateTime createdAt;
    private String hostReply; // opcional, respuesta del anfitrión
}
