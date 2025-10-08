package co.edu.uniquindio.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private UserResponse user;
    private PlaceResponse place;
    private ReplyResponse reply;
}
