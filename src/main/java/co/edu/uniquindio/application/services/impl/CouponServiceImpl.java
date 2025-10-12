package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.CouponRequest;
import co.edu.uniquindio.application.dto.CouponResponse;
import co.edu.uniquindio.application.mappers.CouponMapper;
import co.edu.uniquindio.application.model.Coupon;
import co.edu.uniquindio.application.model.CouponStatus;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.CouponRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.CouponService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {



    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponMapper couponMapper;

    public Coupon findEntityByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Cupón no encontrado"));
    }
    public CouponServiceImpl(CouponRepository couponRepository, UserRepository userRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
        this.couponMapper = couponMapper;
    }

    @Override
    public CouponResponse createCoupon(CouponRequest request) {
        Coupon coupon = couponMapper.toEntity(request);
        coupon.setStatus(CouponStatus.ACTIVE);

        if (request.getHostId() != null) {
            User host = userRepository.findById(request.getHostId())
                    .orElseThrow(() -> new RuntimeException("Host no encontrado"));
            coupon.setHost(host);
        }

        couponRepository.save(coupon);
        return couponMapper.toResponse(coupon);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(couponMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        // Verificar expiración
        if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDate.now())) {
            coupon.setStatus(CouponStatus.EXPIRED);
            couponRepository.save(coupon);
        }
        return couponMapper.toResponse(coupon);
    }

    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        couponRepository.delete(coupon);
    }
}

