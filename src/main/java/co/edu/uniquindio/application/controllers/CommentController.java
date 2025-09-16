package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateCommentDTO dto) {
        // Usuario puede comentar si tuvo reserva completada
        return ResponseEntity.ok(new ResponseDTO<>(true, "Comentario agregado"));
    }

    @GetMapping("/accommodation/{id}")
    public ResponseEntity<ResponseDTO<List<CommentDTO>>> listByAccommodation(@PathVariable Long id) {
        // Listar comentarios por alojamiento
        return ResponseEntity.ok(new ResponseDTO<>(true, List.of()));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ResponseDTO<String>> reply(@PathVariable Long id, @RequestBody String reply) {
        // Host responde a un comentario
        return ResponseEntity.ok(new ResponseDTO<>(true, "Respuesta agregada"));
    }
}
