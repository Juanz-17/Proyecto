package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.EditAccommodationDTO;
import co.edu.uniquindio.application.services.AccommodationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationServiceImpl implements AccommodationService {

    private final List<AccommodationDTO> accommodations = new ArrayList<>();

    @Override
    public void create(CreateAccommodationDTO dto) throws Exception {
        accommodations.add(new AccommodationDTO("1", dto.name(), dto.address(),
                dto.description(), dto.pricePerNight(), dto.capacity(), dto.photos(), dto.hostId()));
    }

    @Override
    public AccommodationDTO get(String id) throws Exception {
        return accommodations.stream()
                .filter(a -> a.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));
    }

    @Override
    public void delete(String id) throws Exception {
        accommodations.removeIf(a -> a.id().equals(id));
    }

    @Override
    public List<AccommodationDTO> listAll() {
        return accommodations;
    }

    @Override
    public void edit(String id, EditAccommodationDTO dto) throws Exception {
        delete(id);
        accommodations.add(new AccommodationDTO(id, dto.name(), dto.address(),
                dto.description(), dto.pricePerNight(), dto.capacity(), dto.photos(), "hostId"));
    }
}
