package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.model.BookingStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer guestCount;
    private Double price;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private UserResponse guest;
    private PlaceResponse place;
}
