package co.edu.uniquindio.application.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.time.LocalDateTime;

@Embeddable
@Data
public class Reply {
    private String message;
    private LocalDateTime repliedAt;
}