package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ReplyRequest;
import co.edu.uniquindio.application.dto.ReviewRequest;
import co.edu.uniquindio.application.dto.ApiResponse;
import co.edu.uniquindio.application.dto.ReviewResponse;
import co.edu.uniquindio.application.mappers.ReviewMapper;
import co.edu.uniquindio.application.model.Review;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.services.PlaceService;
import co.edu.uniquindio.application.services.ReviewService;
import co.edu.uniquindio.application.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "API para gestión de reseñas y calificaciones de alojamientos")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final PlaceService placeService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    @Operation(
            summary = "Crear nueva reseña",
            description = "Crea una reseña para un alojamiento. Requiere que el usuario haya completado una estadía en el alojamiento."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de reseña inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario o alojamiento no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Usuario no autorizado para reseñar este alojamiento"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "El usuario ya reseñó este alojamiento")
    })
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
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
    @Operation(
            summary = "Obtener reseña por ID",
            description = "Recupera los detalles completos de una reseña específica incluyendo calificación, comentario y respuestas."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reseña encontrada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        ReviewResponse response = reviewMapper.toResponse(review);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/place/{placeId}")
    @Operation(
            summary = "Obtener reseñas por alojamiento",
            description = "Lista todas las reseñas de un alojamiento específico, ordenadas por fecha de creación (más recientes primero)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Alojamiento no encontrado")
    })
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByPlace(@PathVariable Long placeId) {
        var place = placeService.getPlaceById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        List<Review> reviews = reviewService.getReviewsByPlace(place);
        List<ReviewResponse> responses = reviews.stream()
                .map(reviewMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Obtener reseñas por usuario",
            description = "Lista todas las reseñas realizadas por un usuario específico."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
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
    @Operation(
            summary = "Actualizar reseña",
            description = "Actualiza el contenido y calificación de una reseña existente. Solo disponible para el usuario que creó la reseña."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para modificar esta reseña")
    })
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {

        Review reviewDetails = reviewMapper.toEntity(request);
        Review updatedReview = reviewService.updateReview(id, reviewDetails);
        ReviewResponse response = reviewMapper.toResponse(updatedReview);

        return ResponseEntity.ok(ApiResponse.success(response, "Reseña actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar reseña",
            description = "Elimina permanentemente una reseña del sistema. Solo disponible para el usuario que creó la reseña o administradores."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reseña eliminada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para eliminar esta reseña")
    })
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reseña eliminada exitosamente"));
    }

    @PostMapping("/{id}/reply")
    @Operation(
            summary = "Agregar respuesta a reseña",
            description = "El anfitrión puede agregar una respuesta a una reseña de su alojamiento."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Respuesta agregada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Mensaje de respuesta inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reseña o anfitrión no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Anfitrión no autorizado para responder esta reseña"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Ya existe una respuesta para esta reseña")
    })
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
    @Operation(
            summary = "Obtener reseñas con respuestas",
            description = "Lista todas las reseñas de los alojamientos de un anfitrión que ya tienen respuestas."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de reseñas con respuestas obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Anfitrión no encontrado")
    })
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
    @Operation(
            summary = "Obtener reseñas sin respuestas",
            description = "Lista todas las reseñas de los alojamientos de un anfitrión que aún no tienen respuestas."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de reseñas sin respuestas obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Anfitrión no encontrado")
    })
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
