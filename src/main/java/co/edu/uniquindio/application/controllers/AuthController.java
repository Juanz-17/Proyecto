package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.LoginRequest;
import co.edu.uniquindio.application.dto.UserRegistrationRequest;
import co.edu.uniquindio.application.dto.ApiResponse;
import co.edu.uniquindio.application.dto.AuthResponse;
import co.edu.uniquindio.application.dto.UserResponse;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.security.JwtTokenProvider;
import co.edu.uniquindio.application.services.AuthService;
import co.edu.uniquindio.application.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para registro, login y gestión de contraseñas de usuarios")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema. Los usuarios pueden registrarse como huéspedes normales o como anfitriones (hosts)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRegistrationRequest request) {

        User user = userMapper.toEntity(request);
        User createdUser = authService.registerUser(user);

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
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica a un usuario con su email y contraseña. Retorna información del usuario y token JWT para acceder a endpoints protegidos."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })

    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        // 🔹 Autenticar al usuario con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 🔹 Generar token JWT real
        String token = jwtTokenProvider.generateToken(authentication);

        // 🔹 Obtener el User desde el AuthService
        User user = (User) authentication.getPrincipal();

        AuthResponse authResponse = new AuthResponse();
        authResponse.setId(user.getId());
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(user.getRole());
        authResponse.setIsHost(user.getIsHost());

        // 🔹 Aquí va el token real
        authResponse.setToken(token);

        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login exitoso"));
    }

    @PostMapping("/password-reset/request")
    @Operation(
            summary = "Solicitar restablecimiento de contraseña",
            description = "Envía un código de verificación al email del usuario para permitir el restablecimiento de su contraseña."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Código enviado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Email no registrado en el sistema")
    })
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @Parameter(description = "Email del usuario que solicita el restablecimiento", required = true, example = "usuario@ejemplo.com")
            @RequestParam(name = "email") String email) {

        authService.requestPasswordReset(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Se ha enviado un código de restablecimiento a tu email"));
    }

    @PostMapping("/password-reset/confirm")
    @Operation(
            summary = "Confirmar restablecimiento de contraseña",
            description = "Valida el código de verificación y actualiza la contraseña del usuario con la nueva contraseña proporcionada."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Código inválido o expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Solicitud de restablecimiento no encontrada")
    })
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Parameter(description = "Código de verificación recibido por email", required = true, example = "ABC123")
            @RequestParam(name = "code") String code,

            @Parameter(description = "Nueva contraseña del usuario", required = true, example = "nuevaContraseña123")
            @RequestParam(name = "newPassword") String newPassword) {

        authService.resetPassword(code, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Contraseña restablecida exitosamente"));
    }
}
