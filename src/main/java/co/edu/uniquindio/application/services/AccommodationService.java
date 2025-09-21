package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.EditAccommodationDTO;
import java.util.List;

public interface AccommodationService {

    void create(CreateAccommodationDTO dto) throws Exception;

    AccommodationDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<AccommodationDTO> listAll();

    void edit(String id, EditAccommodationDTO dto) throws Exception;
}
