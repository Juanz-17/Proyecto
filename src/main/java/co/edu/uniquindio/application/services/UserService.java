package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.CreateUserDTO;
import co.edu.uniquindio.application.dto.EditUserDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import java.util.List;

public interface UserService {

    void create(CreateUserDTO userDTO) throws Exception;

    UserDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<UserDTO> listAll();

    void edit(String id, EditUserDTO userDTO) throws Exception;

}
