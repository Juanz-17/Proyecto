package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.Review;
import co.edu.uniquindio.application.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review createReview(Review review);
    Optional<Review> getReviewById(Long id);
    List<Review> getReviewsByUser(User user);
    List<Review> getReviewsByPlace(Place place);
    Optional<Review> getReviewByUserAndPlace(User user, Place place);
    Review updateReview(Long id, Review reviewDetails);
    void deleteReview(Long id);
    Review addReplyToReview(Long reviewId, String replyMessage, User host);

    // Métricas y estadísticas
    Double getAverageRatingByPlace(Place place);
    long getReviewCountByPlace(Place place);
    List<Review> getReviewsWithRepliesByHost(User host);
    List<Review> getReviewsWithoutRepliesByHost(User host);
    boolean canUserReviewPlace(User user, Place place);
}
