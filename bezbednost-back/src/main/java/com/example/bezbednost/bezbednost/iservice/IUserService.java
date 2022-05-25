package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.dto.ChangePasswordDto;
import com.example.bezbednost.bezbednost.dto.PasswordDto;
import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.exception.InvalidInputException;
import com.example.bezbednost.bezbednost.model.User;

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
}
