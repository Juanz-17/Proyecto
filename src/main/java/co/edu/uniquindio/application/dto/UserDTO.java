package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.model.Role;

public record UserDTO(
        String id,
        String name,
        String email,
        String photoUrl,
        Role role
) {
}
