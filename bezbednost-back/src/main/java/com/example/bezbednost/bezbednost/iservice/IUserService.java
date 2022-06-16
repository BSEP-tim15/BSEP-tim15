package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.config.UserTokenState;
import com.example.bezbednost.bezbednost.dto.*;
import com.example.bezbednost.bezbednost.exception.InvalidInputException;
import com.example.bezbednost.bezbednost.model.User;
import org.springframework.data.jpa.repository.Query;

public interface IUserService {
    User findByUsername(String username);

    User findByEmail(String email);

    User save(UserDto userDto) throws InvalidInputException;

    boolean isUserRegistered(String username);

    String findUserRole(Integer id);

    void sendVerificationEmail(User user);

    boolean verify(String verificationCode);

    void sendRecoveryURL(String email, String token);

    void resetPassword(String email);

    String validatePasswordResetToken(String token);

    void resetPassword(PasswordDto passwordDto);

    String changePassword(User user, ChangePasswordDto changePasswordDto);

    void sendLoginEmail(String email);

    String validatePasswordlessLoginToken(String token);

    User findUserFromToken(String token);

    UserTokenState login2fa(TfaAuthenticationDto tfaAuthenticationDto);

    UserTokenState login(JwtAuthenticationDto jwtAuthenticationDto);

    void changeUsingTwoFactorAuth(Boolean isEnabled, Integer id);

    String getSecretCode(Integer id);
}
