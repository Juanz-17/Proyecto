package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.BookingRequest;
import co.edu.uniquindio.application.dto.BookingResponse;
import co.edu.uniquindio.application.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "place", ignore = true)
    Booking toEntity(BookingRequest request);

    BookingResponse toResponse(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "place", ignore = true)
    void updateEntityFromRequest(BookingRequest request, @MappingTarget Booking booking);
}
