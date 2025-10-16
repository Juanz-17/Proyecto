package co.edu.uniquindio.application;

import co.edu.uniquindio.application.dto.PlaceCreateRequest;
import co.edu.uniquindio.application.dto.PlaceResponse;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.services.impl.PlaceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceServiceImpl placeService;

    private Place place;

    @BeforeEach
    void setUp() {
        place = new Place();
        place.setId(1L);
        place.setName("Casa Campestre");
        place.setCity("Armenia");
        place.setPricePerNight(150.0);
    }

    @Test
    void createPlace_DebeGuardarLugar() {
        // Usamos tu DTO real: PlaceCreateRequest
        PlaceCreateRequest request = new PlaceCreateRequest();
        request.setName("Casa Campestre");
        request.setCity("Armenia");
        request.setPricePerNight(150.0);
        request.setDescription("Bonita casa campestre");
        request.setHostId(1L);

        when(placeRepository.save(any(Place.class))).thenReturn(place);

        PlaceResponse result = placeService.createPlace(request);

        assertNotNull(result);
        assertEquals("Casa Campestre", result.getName());
        verify(placeRepository, times(1)).save(any(Place.class));
    }

    @Test
    void getAllPlaces_DebeRetornarLista() {
        when(placeRepository.findAll()).thenReturn(List.of(place));

        List<PlaceResponse> result = placeService.getAllPlaces();

        assertEquals(1, result.size());
        verify(placeRepository, times(1)).findAll();
    }

    @Test
    void getPlaceById_LugarExistente_DebeRetornarResponse() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

        PlaceResponse result = pla

