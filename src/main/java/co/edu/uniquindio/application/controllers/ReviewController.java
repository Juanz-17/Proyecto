package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ReplyRequest;
import co.edu.uniquindio.application.dto.ReviewRequest; // Cambiado a ReviewRequest
import co.edu.uniquindio.application.dto.ApiResponse;
import co.edu.uniquindio.application.dto.ReviewResponse;
import co.edu.uniquindio.application.mappers.ReviewMapper;
import co.edu.uniquindio.application.model.Review;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.services.PlaceService;
import co.edu.uniquindio.application.services.ReviewService;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final PlaceService placeService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request, // Cambiado a ReviewRequest
            @RequestParam Long userId,
            @RequestParam Long placeId) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var place = placeService.getPlaceById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        // Verificar que el usuario puede reseñar
        if (!reviewService.canUserReviewPlace(user, place)) {
            throw new IllegalArgumentException("No puedes reseñar este alojamiento. Debes haber completado una estadía.");
        }

        Review review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setPlace(place);

        Review createdReview = reviewService.createReview(review);
        ReviewResponse response = reviewMapper.toResponse(createdReview);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Reseña creada exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        ReviewResponse response = reviewMapper.toResponse(review);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByPlace(@PathVariable Long placeId) {
        var place = reviewService.getReviewById(placeId)
                .map(Review::getPlace)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        List<Review> reviews = reviewService.getReviewsByPlace(place);
        List<ReviewResponse> responses = reviews.stream()
                .map(reviewMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Review> reviews = reviewService.getReviewsByUser(user);
        List<ReviewResponse> responses = reviews.stream()
                .map(reviewMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) { // Cambiado a ReviewRequest

        Review reviewDetails = reviewMapper.toEntity(request);
        Review updatedReview = reviewService.updateReview(id, reviewDetails);
        ReviewResponse response = reviewMapper.toResponse(updatedReview);

        return ResponseEntity.ok(ApiResponse.success(response, "Reseña actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reseña eliminada exitosamente"));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReply(
            @PathVariable Long id,
            @Valid @RequestBody ReplyRequest request,
            @RequestParam Long hostId) {

        User host = userService.getUserById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        Review review = reviewService.addReplyToReview(id, request.getMessage(), host);
        ReviewResponse response = reviewMapper.toResponse(review);

        return ResponseEntity.ok(ApiResponse.success(response, "Respuesta agregada exitosamente"));
    }

    @GetMapping("/host/{hostId}/with-replies")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsWithReplies(@PathVariable Long hostId) {
        User host = userService.getUserById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        List<Review> reviews = reviewService.getReviewsWithRepliesByHost(host);
        List<ReviewResponse> responses = reviews.stream()
                .map(reviewMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/host/{hostId}/without-replies")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsWithoutReplies(@PathVariable Long hostId) {
        User host = userService.getUserById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        List<Review> reviews = reviewService.getReviewsWithoutRepliesByHost(host);
        List<ReviewResponse> responses = reviews.stream()
                .map(reviewMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
