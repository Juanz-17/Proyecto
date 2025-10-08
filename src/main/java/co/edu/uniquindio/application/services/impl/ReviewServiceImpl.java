package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.model.Review;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.Reply;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.BookingStatus;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.ReviewRepository;
import co.edu.uniquindio.application.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Review createReview(Review review) {
        // Validaciones básicas
        if (review.getUser() == null) {
            throw new IllegalArgumentException("El usuario es requerido");
        }

        if (review.getPlace() == null) {
            throw new IllegalArgumentException("El alojamiento es requerido");
        }

        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }

        // Verificar que el usuario puede reseñar este alojamiento
        if (!canUserReviewPlace(review.getUser(), review.getPlace())) {
            throw new IllegalArgumentException("No puedes reseñar este alojamiento. Debes haber completado una estadía.");
        }

        // Verificar que no haya reseñado antes este alojamiento
        if (reviewRepository.findByUserAndPlace(review.getUser(), review.getPlace()).isPresent()) {
            throw new IllegalArgumentException("Ya has reseñado este alojamiento");
        }

        // Establecer valores por defecto
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    @Override
    public List<Review> getReviewsByPlace(Place place) {
        return reviewRepository.findByPlaceOrderByCreatedAtDesc(place);
    }

    @Override
    public Optional<Review> getReviewByUserAndPlace(User user, Place place) {
        return reviewRepository.findByUserAndPlace(user, place);
    }

    @Override
    @Transactional
    public Review updateReview(Long id, Review reviewDetails) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        // Solo permitir actualizar rating y comment
        if (reviewDetails.getRating() != null) {
            if (reviewDetails.getRating() < 1 || reviewDetails.getRating() > 5) {
                throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
            }
            existingReview.setRating(reviewDetails.getRating());
        }

        if (reviewDetails.getComment() != null) {
            existingReview.setComment(reviewDetails.getComment());
        }

        return reviewRepository.save(existingReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public Review addReplyToReview(Long reviewId, String replyMessage, User host) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        // Verificar que el host es el dueño del alojamiento
        if (!review.getPlace().getHost().getId().equals(host.getId())) {
            throw new IllegalArgumentException("Solo el anfitrión puede responder a esta reseña");
        }

        // Verificar que no haya ya una respuesta
        if (review.getReply() != null) {
            throw new IllegalArgumentException("Ya has respondido a esta reseña");
        }

        Reply reply = new Reply();
        reply.setMessage(replyMessage);
        reply.setRepliedAt(LocalDateTime.now());

        review.setReply(reply);
        return reviewRepository.save(review);
    }

    @Override
    public Double getAverageRatingByPlace(Place place) {
        return reviewRepository.findAverageRatingByPlace(place).orElse(0.0);
    }

    @Override
    public long getReviewCountByPlace(Place place) {
        return reviewRepository.countByPlace(place);
    }

    @Override
    public List<Review> getReviewsWithRepliesByHost(User host) {
        return reviewRepository.findReviewsWithRepliesByHost(host);
    }

    @Override
    public List<Review> getReviewsWithoutRepliesByHost(User host) {
        return reviewRepository.findReviewsWithoutRepliesByHost(host);
    }

    @Override
    public boolean canUserReviewPlace(User user, Place place) {
        // Verificar que el usuario tiene una reserva COMPLETADA en este alojamiento
        // con fecha de check-out en el pasado
        List<Booking> completedBookings = bookingRepository.findByGuestAndStatus(user, BookingStatus.COMPLETED);

        return completedBookings.stream()
                .anyMatch(booking ->
                        booking.getPlace().getId().equals(place.getId()) &&
                                booking.getCheckOut().isBefore(LocalDateTime.now())
                );
    }
}
