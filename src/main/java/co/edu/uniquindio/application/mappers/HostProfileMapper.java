package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.HostProfileResponse;
import co.edu.uniquindio.application.model.HostProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HostProfileMapper {

    @Mapping(target = "user", ignore = true)
    HostProfileResponse toResponse(HostProfile hostProfile);
}
