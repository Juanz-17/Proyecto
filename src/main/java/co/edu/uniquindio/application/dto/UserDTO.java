package co.edu.uniquindio.application.dto;

import javax.management.relation.Role;

public record UserDTO(
        String id,
        String name,
        String email,
        String photoUrl,
        Role role
) {
}
