package co.edu.uniquindio.application.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Data;

@Embeddable
@Data
public class Address {
    private String address;
    private String city;

    @Embedded
    private Location location;
}
