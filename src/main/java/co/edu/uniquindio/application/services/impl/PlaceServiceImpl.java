package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.Status;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.BookingStatus;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.services.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Place createPlace(Place place) {
        // Validaciones básicas
        if (place.getTitle() == null || place.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es requerido");
        }

        if (place.getDescription() == null || place.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es requerida");
        }

        if (place.getAddress() == null) {
            throw new IllegalArgumentException("La dirección es requerida");
        }

        if (place.getNightlyPrice() == null || place.getNightlyPrice() <= 0) {
            throw new IllegalArgumentException("El precio por noche debe ser mayor a 0");
        }

        if (place.getMaxGuests() == null || place.getMaxGuests() <= 0) {
            throw new IllegalArgumentException("El número máximo de huéspedes debe ser mayor a 0");
        }

        if (place.getImages() == null || place.getImages().isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos una imagen");
        }

        if (place.getImages().size() > 10) {
            throw new IllegalArgumentException("No se pueden agregar más de 10 imágenes");
        }

        // Establecer valores por defecto
        place.setStatus(Status.ACTIVE);
        place.setCreatedAt(LocalDateTime.now());

        return placeRepository.save(place);
    }

    @Override
    public Optional<Place> getPlaceById(Long id) {
        return placeRepository.findById(id);
    }

    @Override
    public List<Place> getPlacesByHost(User host) {
        return placeRepository.findByHost(host);
    }

    @Override
    public List<Place> getPlacesByCity(String city) {
        return placeRepository.findByAddressCityIgnoreCase(city);
    }

    @Override
    public List<Place> getAvailablePlaces(String city, LocalDateTime checkIn, LocalDateTime checkOut,
                                          Integer guests, Double minPrice, Double maxPrice) {
        // Validar fechas
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Las fechas de check-in y check-out son requeridas");
        }

        if (checkIn.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de check-in no puede ser en el pasado");
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new IllegalArgumentException("La fecha de check-out debe ser posterior al check-in");
        }

        if (guests == null || guests <= 0) {
            throw new IllegalArgumentException("El número de huéspedes debe ser mayor a 0");
        }

        return placeRepository.findAvailablePlaces(
                city, checkIn, checkOut, guests,
                minPrice != null ? minPrice : 0.0,
                maxPrice != null ? maxPrice : Double.MAX_VALUE,
                Status.ACTIVE
        );
    }

    @Override
    public List<Place> getPlacesByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice == null) minPrice = 0.0;
        if (maxPrice == null) maxPrice = Double.MAX_VALUE;

        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Los precios no pueden ser negativos");
        }

        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("El precio mínimo no puede ser mayor al precio máximo");
        }

        return placeRepository.findByNightlyPriceBetweenAndStatus(minPrice, maxPrice, Status.ACTIVE);
    }

    @Override
    @Transactional
    public Place updatePlace(Long id, Place placeDetails) {
        Place existingPlace = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        // Actualizar campos permitidos
        if (placeDetails.getTitle() != null) {
            existingPlace.setTitle(placeDetails.getTitle());
        }
        if (placeDetails.getDescription() != null) {
            existingPlace.setDescription(placeDetails.getDescription());
        }
        if (placeDetails.getAddress() != null) {
            existingPlace.setAddress(placeDetails.getAddress());
        }
        if (placeDetails.getNightlyPrice() != null) {
            if (placeDetails.getNightlyPrice() <= 0) {
                throw new IllegalArgumentException("El precio por noche debe ser mayor a 0");
            }
            existingPlace.setNightlyPrice(placeDetails.getNightlyPrice());
        }
        if (placeDetails.getMaxGuests() != null) {
            if (placeDetails.getMaxGuests() <= 0) {
                throw new IllegalArgumentException("El número máximo de huéspedes debe ser mayor a 0");
            }
            existingPlace.setMaxGuests(placeDetails.getMaxGuests());
        }
        if (placeDetails.getServices() != null) {
            existingPlace.setServices(placeDetails.getServices());
        }

        return placeRepository.save(existingPlace);
    }

    @Override
    @Transactional
    public void deletePlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        // Soft delete - cambiar estado a INACTIVE
        place.setStatus(Status.INACTIVE);
        placeRepository.save(place);
    }

    @Override
    @Transactional
    public Place activatePlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        place.setStatus(Status.ACTIVE);
        return placeRepository.save(place);
    }

    @Override
    @Transactional
    public Place deactivatePlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        place.setStatus(Status.INACTIVE);
        return placeRepository.save(place);
    }

    @Override
    public boolean canDeletePlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        // Verificar si tiene reservas futuras
        List<BookingStatus> activeStatuses = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        LocalDateTime now = LocalDateTime.now();

        return place.getBookings().stream()
                .noneMatch(booking ->
                        activeStatuses.contains(booking.getStatus()) &&
                                booking.getCheckIn().isAfter(now)
                );
    }

    @Override
    @Transactional
    public void addImageToPlace(Long placeId, String imageUrl) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        place.addImage(imageUrl);
        placeRepository.save(place);
    }

    @Override
    @Transactional
    public void removeImageFromPlace(Long placeId, String imageUrl) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        if (place.getImages().contains(imageUrl)) {
            place.getImages().remove(imageUrl);

            // Verificar que quede al menos una imagen
            if (place.getImages().isEmpty()) {
                throw new IllegalStateException("No se puede eliminar la última imagen");
            }

            placeRepository.save(place);
        }
    }

    @Override
    public long countPlacesByHost(User host) {
        return placeRepository.countByHost(host);
    }
}