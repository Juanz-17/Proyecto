package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.PlaceCreateRequest;
import co.edu.uniquindio.application.dto.PlaceUpdateRequest;
import co.edu.uniquindio.application.dto.ApiResponse;
import co.edu.uniquindio.application.dto.PlaceResponse;
import co.edu.uniquindio.application.mappers.PlaceMapper;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.services.PlaceService;
import co.edu.uniquindio.application.services.ReviewService;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final PlaceMapper placeMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<PlaceResponse>> createPlace(
            @Valid @RequestBody PlaceCreateRequest request,
            @RequestParam Long hostId) {

        // En una implementación real, el hostId vendría del token JWT
        User host = userService.getUserById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        Place place = placeMapper.toEntity(request);
        place.setHost(host);

        Place createdPlace = placeService.createPlace(place);
        PlaceResponse response = placeMapper.toResponse(createdPlace);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Alojamiento creado exitosamente"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getAllPlaces() {
        List<Place> places = placeService.getPlacesByPriceRange(0.0, Double.MAX_VALUE);
        List<PlaceResponse> responses = places.stream()
                .map(place -> {
                    Double avgRating = reviewService.getAverageRatingByPlace(place);
                    Long reviewCount = reviewService.getReviewCountByPlace(place);
                    return placeMapper.toResponseWithStats(place, avgRating, reviewCount);
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaceResponse>> getPlaceById(@PathVariable Long id) {
        Place place = placeService.getPlaceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        Double avgRating = reviewService.getAverageRatingByPlace(place);
        Long reviewCount = reviewService.getReviewCountByPlace(place);
        PlaceResponse response = placeMapper.toResponseWithStats(place, avgRating, reviewCount);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/available")
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> searchAvailablePlaces(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut,
            @RequestParam Integer guests,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<Place> places = placeService.getAvailablePlaces(city, checkIn, checkOut, guests, minPrice, maxPrice);
        List<PlaceResponse> responses = places.stream()
                .map(place -> {
                    Double avgRating = reviewService.getAverageRatingByPlace(place);
                    Long reviewCount = reviewService.getReviewCountByPlace(place);
                    return placeMapper.toResponseWithStats(place, avgRating, reviewCount);
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getPlacesByHost(@PathVariable Long hostId) {
        User host = userService.getUserById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        List<Place> places = placeService.getPlacesByHost(host);
        List<PlaceResponse> responses = places.stream()
                .map(placeMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaceResponse>> updatePlace(
            @PathVariable Long id,
            @Valid @RequestBody PlaceUpdateRequest request) {

        // Usar el nuevo método del mapper
        Place placeDetails = placeMapper.toEntityFromUpdate(request);
        Place updatedPlace = placeService.updatePlace(id, placeDetails);
        PlaceResponse response = placeMapper.toResponse(updatedPlace);

        return ResponseEntity.ok(ApiResponse.success(response, "Alojamiento actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Alojamiento eliminado exitosamente"));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PlaceResponse>> activatePlace(@PathVariable Long id) {
        Place place = placeService.activatePlace(id);
        PlaceResponse response = placeMapper.toResponse(place);
        return ResponseEntity.ok(ApiResponse.success(response, "Alojamiento activado exitosamente"));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<PlaceResponse>> deactivatePlace(@PathVariable Long id) {
        Place place = placeService.deactivatePlace(id);
        PlaceResponse response = placeMapper.toResponse(place);
        return ResponseEntity.ok(ApiResponse.success(response, "Alojamiento desactivado exitosamente"));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> addImage(
            @PathVariable Long id,
            @RequestParam String imageUrl) {

        placeService.addImageToPlace(id, imageUrl);
        return ResponseEntity.ok(ApiResponse.success(null, "Imagen agregada exitosamente"));
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> removeImage(
            @PathVariable Long id,
            @RequestParam String imageUrl) {

        placeService.removeImageFromPlace(id, imageUrl);
        return ResponseEntity.ok(ApiResponse.success(null, "Imagen eliminada exitosamente"));
    }
}