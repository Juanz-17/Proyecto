package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.ReplyResponse;
import co.edu.uniquindio.application.model.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReplyMapper {

    ReplyResponse toResponse(Reply reply);
}
