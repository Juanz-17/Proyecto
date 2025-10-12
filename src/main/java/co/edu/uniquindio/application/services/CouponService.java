package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.CouponRequest;
import co.edu.uniquindio.application.dto.CouponResponse;

import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    List<CouponResponse> getAllCoupons();
    CouponResponse getCouponByCode(String code);
    void deleteCoupon(Long id);
}

