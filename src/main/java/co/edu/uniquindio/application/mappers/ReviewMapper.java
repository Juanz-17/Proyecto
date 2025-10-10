package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.ReviewRequest;
import co.edu.uniquindio.application.dto.ReviewResponse;
import co.edu.uniquindio.application.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "place", ignore = true)
    @Mapping(target = "reply", ignore = true)
    Review toEntity(ReviewRequest request);

    ReviewResponse toResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "place", ignore = true)
    @Mapping(target = "reply", ignore = true)
    void updateEntityFromRequest(ReviewRequest request, @MappingTarget Review review);
}
