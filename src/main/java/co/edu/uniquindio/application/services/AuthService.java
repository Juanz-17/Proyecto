package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.model.User;

public interface AuthService {
    User registerUser(User user);
    User loginUser(String email, String password);
    void requestPasswordReset(String email);
    boolean resetPassword(String code, String newPassword);
    boolean validatePassword(String password);
    boolean changePassword(Long userId, String currentPassword, String newPassword);
}
