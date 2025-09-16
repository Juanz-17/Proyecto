package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateReservationDTO dto) {
        // Validar disponibilidad y crear reserva
        return ResponseEntity.ok(new ResponseDTO<>(true, "Reserva creada correctamente"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ResponseDTO<String>> cancel(@PathVariable Long id) {
        // Cancelar reserva (según reglas de 48h antes check-in)
        return ResponseEntity.ok(new ResponseDTO<>(true, "Reserva cancelada"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ReservationDTO>>> listUserReservations() {
        // Retorna reservas del usuario autenticado
        return ResponseEntity.ok(new ResponseDTO<>(true, List.of()));
    }

    @GetMapping("/host")
    public ResponseEntity<ResponseDTO<List<ReservationDTO>>> listHostReservations() {
        // Retorna reservas de los alojamientos del host
        return ResponseEntity.ok(new ResponseDTO<>(true, List.of()));
    }
}
