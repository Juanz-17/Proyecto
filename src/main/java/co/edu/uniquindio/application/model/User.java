package co.edu.uniquindio.application.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id; // Podrías cambiarlo a Long con @GeneratedValue si prefieres

    private String name;

    private String phone;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String photoUrl;

    private LocalDate dateBirth;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // Relación con alojamientos (un host puede tener muchos alojamientos)
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Accommodation> accommodations;

    // Relación con reservas (como guest)
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    // Relación con comentarios (como guest)
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}

