package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.CreateReservationDTO;
import co.edu.uniquindio.application.dto.ReservationDTO;

import java.util.List;

public interface ReservationService {

    void create(CreateReservationDTO dto) throws Exception;

    ReservationDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<ReservationDTO> listAll();

    List<ReservationDTO> listByGuest(String guestId);

    List<ReservationDTO> listByHost(String hostId);
}
