package co.edu.uniquindio.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReplyResponse {
    private String message;
    private LocalDateTime repliedAt;
}
