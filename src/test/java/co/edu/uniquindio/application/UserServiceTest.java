package co.edu.uniquindio.application;

import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.Role;
import co.edu.uniquindio.application.model.Status;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private User host;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Juan Pérez");
        user.setEmail("juan@email.com");
        user.setPassword("password123");
        user.setRole(Role.GUEST);
        user.setStatus(Status.ACTIVE);
        user.setIsHost(false);
        user.setCreatedAt(LocalDateTime.now());

        host = new User();
        host.setId(2L);
        host.setName("María García");
        host.setEmail("maria@email.com");
        host.setPassword("password123");
        host.setRole(Role.HOST);
        host.setStatus(Status.ACTIVE);
        host.setIsHost(true);
        host.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createUser_UsuarioValido_DebeCrearUsuario() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.createUser(user);

        // Then
        assertNotNull(result);
        assertEquals("Juan Pérez", result.getName());
        assertEquals("juan@email.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_UsuarioExistente_DebeRetornarUsuario() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Juan Pérez", result.get().getName());
        assertEquals("juan@email.com", result.get().getEmail());
        assertEquals(Role.GUEST, result.get().getRole());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_UsuarioNoExistente_DebeRetornarEmpty() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getUserByEmail_UsuarioExistente_DebeRetornarUsuario() {
        // Given
        when(userRepository.findByEmail("juan@email.com")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getUserByEmail("juan@email.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Juan Pérez", result.get().getName());
        verify(userRepository, times(1)).findByEmail("juan@email.com");
    }

    @Test
    void getUsersByRole_RolExistente_DebeRetornarListaUsuarios() {
        // Given
        List<User> guests = Arrays.asList(user);
        when(userRepository.findByRole(Role.GUEST)).thenReturn(guests);

        // When
        List<User> result = userService.getUsersByRole(Role.GUEST);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(Role.GUEST, result.get(0).getRole());
        verify(userRepository, times(1)).findByRole(Role.GUEST);
    }

    @Test
    void getUsersByStatus_StatusExistente_DebeRetornarListaUsuarios() {
        // Given
        List<User> activeUsers = Arrays.asList(user, host);
        when(userRepository.findByStatus(Status.ACTIVE)).thenReturn(activeUsers);

        // When
        List<User> result = userService.getUsersByStatus(Status.ACTIVE);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(Status.ACTIVE, result.get(0).getStatus());
        verify(userRepository, times(1)).findByStatus(Status.ACTIVE);
    }

    @Test
    void updateUser_UsuarioExistente_DebeActualizarYRetornarUsuario() {
        // Given
        User updatedUser = new User();
        updatedUser.setName("Juan Carlos Pérez");
        updatedUser.setEmail("juancarlos@email.com");
        updatedUser.setPhone("123456789");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(1L, updatedUser);

        // Then
        assertNotNull(result);
        assertEquals("Juan Carlos Pérez", result.getName());
        assertEquals("juancarlos@email.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_UsuarioNoExistente_DebeLanzarExcepcion() {
        // Given
        User updatedUser = new User();
        updatedUser.setName("Usuario Actualizado");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(999L, updatedUser);
        });

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_UsuarioExistente_DebeEliminarUsuario() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_UsuarioNoExistente_DebeLanzarExcepcion() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(999L);
        });

        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void activateUser_UsuarioInactivo_DebeActivarUsuario() {
        // Given
        user.setStatus(Status.INACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.activateUser(1L);

        // Then
        assertEquals(Status.ACTIVE, result.getStatus());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deactivateUser_UsuarioActivo_DebeDesactivarUsuario() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.deactivateUser(1L);

        // Then
        assertEquals(Status.INACTIVE, result.getStatus());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void existsByEmail_EmailExistente_DebeRetornarTrue() {
        // Given
        when(userRepository.existsByEmail("juan@email.com")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("juan@email.com");

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("juan@email.com");
    }

    @Test
    void existsByEmail_EmailNoExistente_DebeRetornarFalse() {
        // Given
        when(userRepository.existsByEmail("noexistente@email.com")).thenReturn(false);

        // When
        boolean result = userService.existsByEmail("noexistente@email.com");

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail("noexistente@email.com");
    }

    @Test
    void convertToHost_UsuarioGuestNoHost_DebeConvertirEnHost() {
        // Given
        String legalDocument = "123456789";
        String aboutMe = "Soy un anfitrión experimentado";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.convertToHost(1L, legalDocument, aboutMe);

        // Then
        assertTrue(result.getIsHost());
        assertEquals(Role.HOST, result.getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void convertToHost_UsuarioYaEsHost_DebeLanzarExcepcion() {
        // Given
        user.setIsHost(true);
        user.setRole(Role.HOST);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.convertToHost(1L, "123456789", "About me");
        });

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void countUsersByRole_RolExistente_DebeRetornarConteo() {
        // Given
        when(userRepository.countByRole(Role.GUEST)).thenReturn(5L);

        // When
        long result = userService.countUsersByRole(Role.GUEST);

        // Then
        assertEquals(5L, result);
        verify(userRepository, times(1)).countByRole(Role.GUEST);
    }
}