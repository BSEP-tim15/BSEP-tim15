package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.model.User;

public interface IUserService {
    User findByUsername(String username);

    User save(UserDto userDto);

    boolean isUserRegistered(String username);

    String findUserRole(Integer id);
}
