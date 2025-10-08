package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.model.Role;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Boolean isHost;
}
