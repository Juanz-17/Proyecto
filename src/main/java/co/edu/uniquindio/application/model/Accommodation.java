package co.edu.uniquindio.application.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "accommodations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String city;
    private String address;

    private double pricePerNight;

    private int maxCapacity;

    @ElementCollection
    private List<String> photos;

    @Enumerated(EnumType.STRING)
    private AccommodationStatus status; // para soft delete

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host; // relación con el anfitrión

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}

