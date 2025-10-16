package co.edu.uniquindio.application;

import co.edu.uniquindio.application.dto.CouponRequest;
import co.edu.uniquindio.application.dto.CouponResponse;
import co.edu.uniquindio.application.model.Coupon;
import co.edu.uniquindio.application.repositories.CouponRepository;
import co.edu.uniquindio.application.services.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("DESC10");
        coupon.setDiscountValue(10);
        coupon.setPercentage(true);
        coupon.setExpirationDate(LocalDate.now().plusDays(10));
    }

    @Test
    void createCoupon_DebeGuardarYRetornarCouponResponse() {
        CouponRequest request = new CouponRequest("DESC10", 10, true, LocalDate.now().plusDays(10), null);
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        CouponResponse response = couponService.createCoupon(request);

        assertNotNull(response);
        assertEquals("DESC10", response.getCode());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void getAllCoupons_DebeRetornarLista() {
        when(couponRepository.findAll()).thenReturn(List.of(coupon));

        List<CouponResponse> result = couponService.getAllCoupons();

        assertEquals(1, result.size());
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void getCouponByCode_CuponExistente_DebeRetornarCouponResponse() {
        when(couponRepository.findByCode("DESC10")).thenReturn(Optional.of(coupon));

        CouponResponse result = couponService.getCouponByCode("DESC10");

        assertNotNull(result);
        assertEquals("DESC10", result.getCode());
        verify(couponRepository, times(1)).findByCode("DESC10");
    }

    @Test
    void deleteCoupon_CuponExistente_DebeEliminarlo() {
        when(couponRepository.existsById(1L)).thenReturn(true);
        doNothing().when(couponRepository).deleteById(1L);

        couponService.deleteCoupon(1L);

        verify(couponRepository, times(1)).deleteById(1L);
    }
}

