package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.PlaceCreateRequest;
import co.edu.uniquindio.application.dto.PlaceUpdateRequest;
import co.edu.uniquindio.application.dto.PlaceResponse;
import co.edu.uniquindio.application.model.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlaceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Place toEntity(PlaceCreateRequest request);

    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    PlaceResponse toResponse(Place place);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateEntityFromRequest(PlaceUpdateRequest request, @MappingTarget Place place);

    // Método para calcular averageRating y reviewCount
    default PlaceResponse toResponseWithStats(Place place, Double averageRating, Long reviewCount) {
        PlaceResponse response = toResponse(place);
        response.setAverageRating(averageRating);
        response.setReviewCount(reviewCount);
        return response;
    }
}
