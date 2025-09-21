package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.CreateUserDTO;
import co.edu.uniquindio.application.dto.EditUserDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import co.edu.uniquindio.application.services.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final List<UserDTO> users = new ArrayList<>();

    @Override
    public void create(CreateUserDTO userDTO) throws Exception {
        // Simulación: se crea un usuario nuevo
        users.add(new UserDTO("1", userDTO.name(), userDTO.email(), userDTO.photoUrl(), userDTO.role()));
    }

    @Override
    public UserDTO get(String id) throws Exception {
        // Simulación: buscar usuario por id
        return users.stream()
                .filter(u -> u.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
    }

    @Override
    public void delete(String id) throws Exception {
        users.removeIf(u -> u.id().equals(id));
    }

    @Override
    public List<UserDTO> listAll() {
        return users;
    }

    @Override
    public void edit(String id, EditUserDTO userDTO) throws Exception {
        delete(id);
        users.add(new UserDTO(id, userDTO.name(), "email@example.com", userDTO.photoUrl(), userDTO.role()));
    }
}
