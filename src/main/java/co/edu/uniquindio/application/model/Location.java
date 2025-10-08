package co.edu.uniquindio.application.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Location {
    private Double latitude;
    private Double longitude;
}
