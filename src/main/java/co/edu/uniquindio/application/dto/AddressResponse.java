package co.edu.uniquindio.application.dto;

import lombok.Data;

@Data
public class AddressResponse {
    private String address;
    private String city;
    private LocationResponse location;
}