package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.model.Role;
import co.edu.uniquindio.application.model.Status;
import co.edu.uniquindio.application.model.HostProfile;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.HostProfileRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HostProfileRepository hostProfileRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        // Validar que el email no exista
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar datos requeridos
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }

        // Establecer valores por defecto
        user.setStatus(Status.ACTIVE);
        user.setRole(Role.GUEST);
        user.setIsHost(false);

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getUsersByStatus(Status status) {
        return userRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar que el nuevo email no esté en uso por otro usuario
        if (userDetails.getEmail() != null &&
                !userDetails.getEmail().equals(existingUser.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso por otro usuario");
        }

        // Actualizar campos permitidos
        if (userDetails.getName() != null) {
            existingUser.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            existingUser.setPhone(userDetails.getPhone());
        }
        if (userDetails.getPhotoUrl() != null) {
            existingUser.setPhotoUrl(userDetails.getPhotoUrl());
        }
        if (userDetails.getDateBirth() != null) {
            existingUser.setDateBirth(userDetails.getDateBirth());
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Soft delete - cambiar estado a INACTIVE
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setStatus(Status.ACTIVE);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setStatus(Status.INACTIVE);
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User convertToHost(Long userId, String legalDocument, String aboutMe) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si ya es host
        if (user.getIsHost()) {
            throw new IllegalArgumentException("El usuario ya es anfitrión");
        }

        // Crear perfil de host
        HostProfile hostProfile = new HostProfile();
        hostProfile.setUser(user);
        hostProfile.setLegalDocument(legalDocument);
        hostProfile.setAboutMe(aboutMe);

        // Actualizar usuario
        user.setRole(Role.HOST);
        user.setIsHost(true);
        user.setHostProfile(hostProfile);

        return userRepository.save(user);
    }

    @Override
    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
