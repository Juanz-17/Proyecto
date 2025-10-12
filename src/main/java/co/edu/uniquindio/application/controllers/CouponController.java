package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.CouponRequest;
import co.edu.uniquindio.application.dto.CouponResponse;
import co.edu.uniquindio.application.services.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> create(@RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.createCoupon(request));
    }

    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAll() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{code}")
    public ResponseEntity<CouponResponse> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(couponService.getCouponByCode(code));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}

