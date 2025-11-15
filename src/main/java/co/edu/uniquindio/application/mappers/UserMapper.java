package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.UserRegistrationRequest;
import co.edu.uniquindio.application.dto.UserResponse;
import co.edu.uniquindio.application.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = HostProfileMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isHost", ignore = true)
    @Mapping(target = "hostProfile", ignore = true)
    @Mapping(target = "passwordResetCodes", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "places", ignore = true)
    User toEntity(UserRegistrationRequest request);

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isHost", ignore = true)
    @Mapping(target = "hostProfile", ignore = true)
    @Mapping(target = "passwordResetCodes", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "places", ignore = true)
    @Mapping(target = "dateBirth", source = "dateBirth")
    void updateEntityFromRequest(UserRegistrationRequest request, @MappingTarget User user);
}
