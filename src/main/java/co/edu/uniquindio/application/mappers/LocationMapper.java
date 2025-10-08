package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.LocationResponse;
import co.edu.uniquindio.application.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocationMapper {

    LocationResponse toResponse(Location location);
}
