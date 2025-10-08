package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.model.Role;
import co.edu.uniquindio.application.model.Status;
import co.edu.uniquindio.application.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    List<User> getUsersByRole(Role role);
    List<User> getUsersByStatus(Status status);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    User activateUser(Long id);
    User deactivateUser(Long id);
    boolean existsByEmail(String email);
    User convertToHost(Long userId, String legalDocument, String aboutMe);
    long countUsersByRole(Role role);
}
