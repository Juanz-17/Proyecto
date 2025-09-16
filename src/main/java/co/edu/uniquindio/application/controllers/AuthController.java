package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<String>> register(@Valid @RequestBody CreateUserDTO dto) throws Exception {
        // Lógica de registro (rol USER por defecto, o HOST si aplica)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(true, "Usuario registrado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<TokenDTO>> login(@Valid @RequestBody LoginDTO dto) throws Exception {
        // Lógica de autenticación con JWT
        TokenDTO token = new TokenDTO("fake-jwt-token");
        return ResponseEntity.ok(new ResponseDTO<>(true, token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO<String>> forgotPassword(@RequestParam String email) {
        // Generar token de recuperación y enviar al correo
        return ResponseEntity.ok(new ResponseDTO<>(true, "Se envió el código de recuperación al correo"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO<String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        // Validar token y actualizar contraseña
        return ResponseEntity.ok(new ResponseDTO<>(true, "La contraseña ha sido restablecida"));
    }
}
