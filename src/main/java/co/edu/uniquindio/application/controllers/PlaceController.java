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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Alojamientos", description = "API para gestión de alojamientos (propiedades, casas, departamentos)")
public class PlaceController {

    private final PlaceService placeService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final PlaceMapper placeMapper;

    @PostMapping
    @Operation(
            summary = "Crear nuevo alojamiento",
            description = "Registra un nuevo alojamiento en el sistema. Requiere que el usuario sea anfitrión y proporciona detalles completos de la propiedad."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Alojamiento creado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos del alojamiento inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Anfitrión no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Usuario no autorizado como anfitrión")
    })
    public ResponseEntity<ApiResponse<PlaceResponse>> createPlace(
            @Valid @RequestBody PlaceCreateRequest request,
            @Parameter(description = "ID del anfitrión que crea el alojamiento", required = true, example = "1")
            @RequestParam Long hostId) {

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
    @Operation(
            summary = "Listar todos los alojamientos",
            description = "Obtiene la lista completa de alojamientos disponibles en el sistema, incluyendo calificaciones y conteo de reseñas."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de alojamientos obtenida exitosamente")
    })
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
    @Operation(
            summary = "Obtener alojamiento por ID",
            description = "Recupera los detalles completos de un alojamiento específico, incluyendo información del anfitrión, calificación promedio y reseñas."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Alojamiento encontrado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Alojamiento no encontrado")
    })
    public ResponseEntity<ApiResponse<PlaceResponse>> getPlaceById(
            @Parameter(name = "id", description = "ID único del alojamiento", required = true, example = "1")
            @PathVariable("id") Long id) {

        Place place = placeService.getPlaceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        Double avgRating = reviewService.getAverageRatingByPlace(place);
        Long reviewCount = reviewService.getReviewCountByPlace(place);
        PlaceResponse response = placeMapper.toResponseWithStats(place, avgRating, reviewCount);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/available")
    @Operation(
            summary = "Buscar alojamientos disponibles",
            description = "Busca alojamientos disponibles según criterios específicos: ciudad, fechas de check-in/check-out, número de huéspedes y rango de precios."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Búsqueda completada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos")
    })
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> searchAvailablePlaces(
            @Parameter(description = "Ciudad donde buscar alojamientos", required = true, example = "Bogotá")
            @RequestParam String city,

            @Parameter(description = "Fecha y hora de check-in (formato: YYYY-MM-DDTHH:mm:ss)", required = true, example = "2024-12-25T15:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,

            @Parameter(description = "Fecha y hora de check-out (formato: YYYY-MM-DDTHH:mm:ss)", required = true, example = "2024-12-30T11:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut,

            @Parameter(description = "Número de huéspedes", required = true, example = "2")
            @RequestParam Integer guests,

            @Parameter(description = "Precio mínimo por noche", required = false, example = "50.0")
            @RequestParam(required = false) Double minPrice,

            @Parameter(description = "Precio máximo por noche", required = false, example = "200.0")
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
    @Operation(
            summary = "Obtener alojamientos por anfitrión",
            description = "Lista todos los alojamientos gestionados por un anfitrión específico."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de alojamientos obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Anfitrión no encontrado")
    })
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getPlacesByHost(
            @Parameter(name = "hostId", description = "ID del anfitrión", required = true, example = "1")
            @PathVariable("hostId") Long hostId) {

        User host = userService.getUserById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        List<Place> places = placeService.getPlacesByHost(host);
        List<PlaceResponse> responses = places.stream()
                .map(placeMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar alojamiento",
            description = "Actualiza la información de un alojamiento existente. Solo disponible para el anfitrión propietario."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Alojamiento actualizado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Alojamiento no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para modificar este alojamiento")
    })
    public ResponseEntity<ApiResponse<PlaceResponse>> updatePlace(
            @Parameter(name = "id", description = "ID del alojamiento a actualizar", required = true, example = "1")
            @PathVariable("id") Long id,

            @Valid @RequestBody PlaceUpdateRequest request) {

        Place existingPlace = placeService.getPlaceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        placeMapper.updateEntityFromRequest(request, existingPlace);

        Place updatedPlace = placeService.updatePlace(id, existingPlace);
        PlaceResponse response = placeMapper.toResponse(updatedPlace);

        return ResponseEntity.ok(ApiResponse.success(response, "Alojamiento actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar alojamiento",
            description = "Elimina permanentemente un alojamiento del sistema. Solo disponible para el anfitrión propietario."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Alojamiento eliminado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Alojamiento no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para eliminar este alojamiento"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede eliminar, tiene reservas activas")
    })
    public ResponseEntity<ApiResponse<Void>> deletePlace(
            @Parameter(name = "id", description = "ID del alojamiento a eliminar", required = true, example = "1")
            @PathVariable("id") Long id) {

        placeService.deletePlace(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Alojamiento eliminado exitosamente"));
    }

    @PostMapping("/{id}/activate")
    @Operation(
            summary = "Activar alojamiento",
            description = "Cambia el estado de un alojamiento a activo, haciéndolo disponible para reservas."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Alojamiento activado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Alojamiento no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para activar este alojamiento")
    })
    public ResponseEntity<ApiResponse<PlaceResponse>> activatePlace(
            @Parameter(name = "id",description = "ID del alojamiento a activar", required = true, example = "1")
            @PathVariable("id") Long id) {

        Place place = placeService.activatePlace(id);
        PlaceResponse response = placeMapper.toResponse(place);
        return ResponseEntity.ok(ApiResponse.success(response, "Alojamiento activado exitosamente"));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(
            summary = "Desactivar alojamiento",
            description = "Cambia el estado de un alojamiento a inactivo, evitando nuevas reservas pero manteniendo las existentes."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Alojamiento desactivado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Alojamiento no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para desactivar este alojamiento")
    })
    public ResponseEntity<ApiResponse<PlaceResponse>> deactivatePlace(
            @Parameter(name = "id", description = "ID del alojamiento a desactivar", required = true, example = "1")
            @PathVariable("id") Long id) {

        Place place = placeService.deactivatePlace(id);
        PlaceResponse response = placeMapper.toResponse(place);
        return ResponseEntity.ok(ApiResponse.success(response, "Alojamiento desactivado exitosamente"));
    }

    @PostMapping("/{id}/images")
    @Operation(
            summary = "Agregar imagen al alojamiento",
            description = "Añade una nueva imagen a la galería de un alojamiento específico."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Imagen agregada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Alojamiento no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para modificar este alojamiento"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "URL de imagen inválida")
    })
    public ResponseEntity<ApiResponse<Void>> addImage(
            @Parameter(description = "ID del alojamiento", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "URL de la imagen a agregar", required = true, example = "https://example.com/image.jpg")
            @RequestParam String imageUrl) {

        placeService.addImageToPlace(id, imageUrl);
        return ResponseEntity.ok(ApiResponse.success(null, "Imagen agregada exitosamente"));
    }

    @DeleteMapping("/{id}/images")
    @Operation(
            summary = "Eliminar imagen del alojamiento",
            description = "Remueve una imagen específica de la galería de un alojamiento."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Imagen eliminada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Alojamiento o imagen no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para modificar este alojamiento")
    })
    public ResponseEntity<ApiResponse<Void>> removeImage(
            @Parameter(description = "ID del alojamiento", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "URL de la imagen a eliminar", required = true, example = "https://example.com/image.jpg")
            @RequestParam String imageUrl) {

        placeService.removeImageFromPlace(id, imageUrl);
        return ResponseEntity.ok(ApiResponse.success(null, "Imagen eliminada exitosamente"));
    }
}