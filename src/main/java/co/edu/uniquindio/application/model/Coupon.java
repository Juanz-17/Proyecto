package co.edu.uniquindio.application.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private double discountValue;

    @Column(nullable = false)
    private boolean percentage; // true = % descuento, false = valor fijo

    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    public boolean isValid() {
        return status == CouponStatus.ACTIVE &&
                (expirationDate == null || !expirationDate.isBefore(LocalDate.now()));
    }

    // Opcional: si los cupones pertenecen a un anfitrión
    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;
}

