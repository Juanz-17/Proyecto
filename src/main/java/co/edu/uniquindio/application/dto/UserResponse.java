package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.model.Role;
import co.edu.uniquindio.application.model.Status;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDate dateBirth;
    private String phone;
    private String photoUrl;
    private Role role;
    private Status status;
    private Boolean isHost;
    private LocalDateTime createdAt;
    private HostProfileResponse hostProfile;
}