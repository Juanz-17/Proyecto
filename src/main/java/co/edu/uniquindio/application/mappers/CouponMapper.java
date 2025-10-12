package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.CouponRequest;
import co.edu.uniquindio.application.dto.CouponResponse;
import co.edu.uniquindio.application.model.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public CouponResponse toResponse(Coupon entity) {
        return CouponResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .discountValue(entity.getDiscountValue())
                .percentage(entity.isPercentage())
                .expirationDate(entity.getExpirationDate())
                .status(entity.getStatus().name())
                .hostName(entity.getHost() != null ? entity.getHost().getName() : null)
                .build();
    }

    public Coupon toEntity(CouponRequest dto) {
        return Coupon.builder()
                .code(dto.getCode())
                .discountValue(dto.getDiscountValue())
                .percentage(dto.isPercentage())
                .expirationDate(dto.getExpirationDate())
                .build();
    }
}

