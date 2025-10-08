package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.UserRegistrationRequest;
import co.edu.uniquindio.application.dto.ApiResponse;
import co.edu.uniquindio.application.dto.UserResponse;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationRequest request) {

        User userDetails = userMapper.toEntity(request);
        User updatedUser = userService.updateUser(id, userDetails);

        UserResponse response = userMapper.toResponse(updatedUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario eliminado exitosamente"));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable Long id) {
        User user = userService.activateUser(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario activado exitosamente"));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Long id) {
        User user = userService.deactivateUser(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario desactivado exitosamente"));
    }

    @PostMapping("/{id}/convert-to-host")
    public ResponseEntity<ApiResponse<UserResponse>> convertToHost(
            @PathVariable Long id,
            @RequestParam String legalDocument,
            @RequestParam String aboutMe) {

        User user = userService.convertToHost(id, legalDocument, aboutMe);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario convertido a anfitrión exitosamente"));
    }
}