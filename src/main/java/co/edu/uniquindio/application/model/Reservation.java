package co.edu.uniquindio.application.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class Reservation {
    private Long id;
    private Accommodation accommodation;
    private User guest;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numberOfGuests;
    private ReservationStatus status;
}
