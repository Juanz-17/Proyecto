package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.LoginRequest;
import co.edu.uniquindio.application.dto.UserRegistrationRequest;
import co.edu.uniquindio.application.dto.ApiResponse;
import co.edu.uniquindio.application.dto.AuthResponse;
import co.edu.uniquindio.application.dto.UserResponse;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.services.AuthService;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRegistrationRequest request) {

        User user = userMapper.toEntity(request);
        User createdUser = authService.registerUser(user);

        // Si se registra como host, crear el perfil de host
        if (Boolean.TRUE.equals(request.getIsHost())) {
            userService.convertToHost(
                    createdUser.getId(),
                    request.getLegalDocument(),
                    request.getAboutMe()
            );
        }

        UserResponse response = userMapper.toResponse(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Usuario registrado exitosamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authService.loginUser(request.getEmail(), request.getPassword());

        // En una implementación real, aquí generarías el JWT token
        AuthResponse authResponse = new AuthResponse();
        authResponse.setId(user.getId());
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(user.getRole());
        authResponse.setIsHost(user.getIsHost());
        authResponse.setToken("jwt-token-here"); // Placeholder

        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login exitoso"));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Se ha enviado un código de restablecimiento a tu email"));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String code,
            @RequestParam String newPassword) {

        authService.resetPassword(code, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Contraseña restablecida exitosamente"));
    }
}
