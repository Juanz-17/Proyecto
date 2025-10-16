package co.edu.uniquindio.application;

import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        user.setPassword("password123");
    }

    @Test
    void registerUser_UsuarioValido_DebeRegistrarse() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.registerUser(user);

        assertNotNull(result);
        assertEquals("test@email.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void loginUser_CredencialesValidas_DebeIniciarSesion() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        User result = authService.loginUser("test@email.com", "password123");

        assertNotNull(result);
        assertEquals("test@email.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("test@email.com");
    }

    @Test
    void loginUser_CredencialesInvalidas_DebeLanzarExcepcion() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            authService.loginUser("test@email.com", "password123");
        });
    }

    @Test
    void validatePassword_ContraseñaValida_DebeRetornarTrue() {
        assertTrue(authService.validatePassword("validPass123"));
    }

    @Test
    void validatePassword_ContraseñaInvalida_DebeRetornarFalse() {
        assertFalse(authService.validatePassword("123"));
    }
}

