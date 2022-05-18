package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.model.User;

public interface IUserService {
    User findByUsername(String username);

    User findByEmail(String email);

    User save(UserDto userDto);

    boolean isUserRegistered(String username);

    String findUserRole(Integer id);

    void sendVerificationEmail(User user);

    boolean verify(String verificationCode);

    void sendRecoveryURL(String email, String token);

    void resetPassword(String email);
}
