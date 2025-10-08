package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.PasswordResetCode;
import co.edu.uniquindio.application.repositories.PasswordResetCodeRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(User user) {
        // Validar que el email no exista
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar contraseña
        if (!validatePassword(user.getPassword())) {
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos de seguridad");
        }

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Establecer valores por defecto
        user.setStatus(co.edu.uniquindio.application.model.Status.ACTIVE);
        user.setRole(co.edu.uniquindio.application.model.Role.GUEST);
        user.setIsHost(false);

        return userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmailAndStatus(email, co.edu.uniquindio.application.model.Status.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas o usuario inactivo"));

        // Verificar contraseña
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return user;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Eliminar códigos anteriores del usuario
        passwordResetCodeRepository.deleteByUser(user);

        // Generar nuevo código
        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setCode(code);
        resetCode.setUser(user);
        resetCode.setCreatedAt(LocalDateTime.now());

        passwordResetCodeRepository.save(resetCode);

        // En un caso real, aquí enviaríamos el código por email
        System.out.println("Código de restablecimiento para " + email + ": " + code);
    }

    @Override
    @Transactional
    public boolean resetPassword(String code, String newPassword) {
        PasswordResetCode resetCode = passwordResetCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido"));

        // Verificar que el código no haya expirado (24 horas)
        if (resetCode.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
            passwordResetCodeRepository.delete(resetCode);
            throw new IllegalArgumentException("El código ha expirado");
        }

        // Validar nueva contraseña
        if (!validatePassword(newPassword)) {
            throw new IllegalArgumentException("La nueva contraseña no cumple con los requisitos de seguridad");
        }

        // Actualizar contraseña
        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Eliminar código usado
        passwordResetCodeRepository.delete(resetCode);

        return true;
    }

    @Override
    public boolean validatePassword(String password) {
        // Mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número
        return password != null &&
                password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*");
    }
}
