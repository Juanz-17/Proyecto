package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.model.Service;
import co.edu.uniquindio.application.model.Status;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PlaceResponse {
    private Long id;
    private String title;
    private String description;
    private AddressResponse address;
    private Double nightlyPrice;
    private Integer maxGuests;
    private List<String> images;
    private List<Service> services;
    private Status status;
    private LocalDateTime createdAt;
    private UserResponse host;
    private Double averageRating;
    private Long reviewCount;
}