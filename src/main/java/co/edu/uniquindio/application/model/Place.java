package co.edu.uniquindio.application.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "places")
@Data
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Column(nullable = false)
    private Double nightlyPrice;

    @Column(nullable = false)
    private Integer maxGuests;

    @ElementCollection
    @CollectionTable(name = "place_images", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "place_services", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "service")
    private List<Service> services = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // Método helper para agregar imagen
    public void addImage(String imageUrl) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        if (this.images.size() < 10) {
            this.images.add(imageUrl);
        } else {
            throw new IllegalStateException("No se pueden agregar más de 10 imágenes");
        }
    }

    @Column(name = "main_image_index")
    private Integer mainImageIndex = 0; // Índice de la imagen principal

    // Método helper para obtener la URL de la imagen principal
    public String getMainImageUrl() {
        if (images != null && !images.isEmpty() && mainImageIndex < images.size()) {
            return images.get(mainImageIndex);
        }
        return images != null && !images.isEmpty() ? images.get(0) : null;
    }

    // Método para cambiar la imagen principal
    public void setMainImage(int index) {
        if (images != null && index >= 0 && index < images.size()) {
            this.mainImageIndex = index;
        }
    }

}