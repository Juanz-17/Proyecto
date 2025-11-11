package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.UserRegistrationRequest;
import co.edu.uniquindio.application.dto.ApiResponse;
import co.edu.uniquindio.application.dto.UserResponse;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema (huéspedes y anfitriones)")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Recupera la información completa de un usuario específico utilizando su ID único."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(name = "id", description = "ID único del usuario", required = true, example = "1")
            @PathVariable("id") Long id) {

        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email/{email}")
    @Operation(
            summary = "Obtener usuario por email",
            description = "Busca y recupera un usuario específico utilizando su dirección de email."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @Parameter(name = "email", description = "Email del usuario a buscar", required = true, example = "usuario@ejemplo.com")
            @PathVariable("email") String email) {

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza la información de perfil de un usuario existente. Campos como email pueden requerir verificación adicional."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "El email ya está en uso por otro usuario")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(name = "id", description = "ID del usuario a actualizar", required = true, example = "1")
            @PathVariable("id") Long id,

            @Valid @RequestBody UserRegistrationRequest request) {

        User userDetails = userMapper.toEntity(request);
        User updatedUser = userService.updateUser(id, userDetails);

        UserResponse response = userMapper.toResponse(updatedUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina permanentemente una cuenta de usuario del sistema. Esta acción puede estar sujeta a restricciones si el usuario tiene reservas activas."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado para eliminar este usuario"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede eliminar, usuario tiene reservas activas o alojamientos")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(name = "id", description = "ID del usuario a eliminar", required = true, example = "1")
            @PathVariable("id") Long id) {

        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario eliminado exitosamente"));
    }

    @PostMapping("/{id}/activate")
    @Operation(
            summary = "Activar usuario",
            description = "Reactiva una cuenta de usuario que estaba desactivada, permitiéndole acceder nuevamente al sistema."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario activado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(
            @Parameter(name = "id", description = "ID del usuario a activar", required = true, example = "1")
            @PathVariable("id") Long id) {

        User user = userService.activateUser(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario activado exitosamente"));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(
            summary = "Desactivar usuario",
            description = "Desactiva temporalmente una cuenta de usuario, impidiendo que acceda al sistema pero manteniendo sus datos."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede desactivar, usuario tiene reservas activas")
    })
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(
            @Parameter(name = "id", description = "ID del usuario a desactivar", required = true, example = "1")
            @PathVariable("id") Long id) {

        User user = userService.deactivateUser(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario desactivado exitosamente"));
    }

    @PostMapping("/{id}/convert-to-host")
    @Operation(
            summary = "Convertir usuario a anfitrión",
            description = "Convierte un usuario huésped en anfitrión, permitiéndole publicar y gestionar alojamientos. Requiere documentación legal e información de perfil."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario convertido a anfitrión exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Documentación legal o información de perfil inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "El usuario ya es anfitrión")
    })
    public ResponseEntity<ApiResponse<UserResponse>> convertToHost(
            @Parameter(name = "id", description = "ID del usuario a convertir", required = true, example = "1")
            @PathVariable("id") Long id,

            @Parameter(description = "Documento legal de identificación", required = true, example = "123456789")
            @RequestParam String legalDocument,

            @Parameter(description = "Información sobre el anfitrión", required = true, example = "Soy un anfitrión experimentado...")
            @RequestParam String aboutMe) {

        User user = userService.convertToHost(id, legalDocument, aboutMe);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario convertido a anfitrión exitosamente"));
    }
}