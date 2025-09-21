package co.edu.uniquindio.application.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class Accommodation {
    private Long id;
    private String title;
    private String description;
    private String city;
    private String address;
    private double pricePerNight;
    private int maxCapacity;
    private List<String> photos;
    private AccommodationStatus status; // para soft delete
    private User host; // relación con el anfitrión
}
