package co.edu.uniquindio.application.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private double discountValue;
    private boolean percentage;
    private LocalDate expirationDate;
    private String status;
    private String hostName;
}

