package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationController {

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateAccommodationDTO dto) {
        // Lógica para crear alojamiento (solo HOST)
        return ResponseEntity.ok(new ResponseDTO<>(true, "Alojamiento creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable Long id,
                                                    @Valid @RequestBody EditAccommodationDTO dto) {
        // Lógica para editar alojamiento
        return ResponseEntity.ok(new ResponseDTO<>(true, "Alojamiento actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable Long id) {
        // Soft delete (estado eliminado = true)
        return ResponseEntity.ok(new ResponseDTO<>(true, "Alojamiento eliminado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AccommodationDTO>> get(@PathVariable Long id) {
        // Retorna detalle del alojamiento
        return ResponseEntity.ok(new ResponseDTO<>(true, new AccommodationDTO(
                "id",
                "Hotel 1",
                "Dirección",
                "Descripción",
                90.000,
                3,
                List.of(),
                "id")));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<AccommodationDTO>>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String services,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Buscar alojamientos con filtros + paginación
        return ResponseEntity.ok(new ResponseDTO<>(true, List.of()));
    }
}
