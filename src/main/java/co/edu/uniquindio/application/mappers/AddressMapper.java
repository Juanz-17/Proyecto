package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.AddressRequest;
import co.edu.uniquindio.application.dto.AddressResponse;
import co.edu.uniquindio.application.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = LocationMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {

    Address toEntity(AddressRequest request);

    AddressResponse toResponse(Address address);

    void updateEntityFromRequest(AddressRequest request, @MappingTarget Address address);
}
