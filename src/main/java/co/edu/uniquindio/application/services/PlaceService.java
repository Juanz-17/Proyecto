package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlaceService {
    Place createPlace(Place place);
    Optional<Place> getPlaceById(Long id);
    List<Place> getPlacesByHost(User host);
    List<Place> getPlacesByCity(String city);
    List<Place> getAvailablePlaces(String city, LocalDateTime checkIn, LocalDateTime checkOut,
                                   Integer guests, Double minPrice, Double maxPrice);
    List<Place> getPlacesByPriceRange(Double minPrice, Double maxPrice);
    Place updatePlace(Long id, Place placeDetails);
    void deletePlace(Long id);
    Place activatePlace(Long id);
    Place deactivatePlace(Long id);
    boolean canDeletePlace(Long placeId);
    void addImageToPlace(Long placeId, String imageUrl);
    void removeImageFromPlace(Long placeId, String imageUrl);
    long countPlacesByHost(User host);
}
