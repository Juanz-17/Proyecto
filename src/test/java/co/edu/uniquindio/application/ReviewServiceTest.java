package co.edu.uniquindio.application;

import co.edu.uniquindio.application.dto.ReviewRequest;
import co.edu.uniquindio.application.dto.ReviewResponse;
import co.edu.uniquindio.application.model.Review;
import co.edu.uniquindio.application.repositories.ReviewRepository;
import co.edu.uniquindio.application.services.impl.ReviewServiceImpl;
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
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;

    @BeforeEach
    void setUp() {
        review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Excelente experiencia");
        review.setDate(LocalDate.now());
    }

    @Test
    void createReview_DebeGuardarYRetornarReviewResponse() {
        ReviewRequest request = new ReviewRequest(5, "Excelente experiencia", 1L, 1L);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponse response = reviewService.createReview(request);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void getAllReviews_DebeRetornarLista() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));

        List<ReviewResponse> result = reviewService.getAllReviews();

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void getReviewById_Existente_DebeRetornarResponse() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        ReviewResponse result = reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals("Excelente experiencia", result.getComment());
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void deleteReview_Existente_DebeEliminar() {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }
}

