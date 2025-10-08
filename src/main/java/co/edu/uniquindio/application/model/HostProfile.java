package co.edu.uniquindio.application.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "host_profiles")
@Data
public class HostProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legal_document")
    private String legalDocument;

    private String aboutMe;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
