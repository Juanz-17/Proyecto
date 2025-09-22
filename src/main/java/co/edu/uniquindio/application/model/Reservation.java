package co.edu.uniquindio.application.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private User guest;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private int numberOfGuests;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}


