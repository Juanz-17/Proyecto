package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.CreateReservationDTO;
import co.edu.uniquindio.application.dto.ReservationDTO;
import co.edu.uniquindio.application.services.AccommodationService;
import co.edu.uniquindio.application.services.ReservationService;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final List<ReservationDTO> reservations = new ArrayList<>();
    private final AccommodationService accommodationService;

    // Inyección por constructor (Spring la detectará automáticamente)
    public ReservationServiceImpl(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @Override
    public void create(CreateReservationDTO dto) throws Exception {
        // Calcular precio total intentando obtener el precio por noche del alojamiento
        double totalPrice = 0.0;
        try {
            AccommodationDTO acc = accommodationService.get(dto.accommodationId());
            if (acc != null && acc.pricePerNight() != null) {
                long nights = ChronoUnit.DAYS.between(dto.startDate(), dto.endDate());
                if (nights <= 0) nights = 1; // mínimo 1 noche, por seguridad
                totalPrice = acc.pricePerNight() * nights;
            }
        } catch (Exception e) {
            // Si no se puede obtener el alojamiento, dejamos totalPrice = 0.0
        }

        String id = UUID.randomUUID().toString();
        ReservationDTO reservation = new ReservationDTO(
                id,
                dto.userId(),
                dto.accommodationId(),
                dto.startDate(),
                dto.endDate(),
                dto.guests(),
                totalPrice,
                "PENDING" // estado inicial
        );

        reservations.add(reservation);
    }

    @Override
    public ReservationDTO get(String id) throws Exception {
        return reservations.stream()
                .filter(r -> r.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Reserva no encontrada"));
    }

    @Override
    public void delete(String id) throws Exception {
        boolean removed = reservations.removeIf(r -> r.id().equals(id));
        if (!removed) {
            throw new Exception("Reserva no encontrada");
        }
    }

    @Override
    public List<ReservationDTO> listAll() {
        return new ArrayList<>(reservations);
    }

    @Override
    public List<ReservationDTO> listByGuest(String guestId) {
        return reservations.stream()
                .filter(r -> r.userId().equals(guestId))
                .toList();
    }

    @Override
    public List<ReservationDTO> listByHost(String hostId) {
        List<ReservationDTO> result = new ArrayList<>();
        for (ReservationDTO r : reservations) {
            try {
                AccommodationDTO acc = accommodationService.get(r.accommodationId());
                if (acc != null && hostId.equals(acc.ownerId())) {
                    result.add(r);
                }
            } catch (Exception e) {
                // ignorar alojamientos que no se puedan resolver
            }
        }
        return result;
    }
}
