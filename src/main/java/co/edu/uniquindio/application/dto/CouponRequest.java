package co.edu.uniquindio.application.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRequest {
    private String code;
    private double discountValue;
    private boolean percentage;
    private LocalDate expirationDate;
    private Long hostId; // opcional si está asociado a un anfitrión
}
