package com.example.bezbednost.bezbednost.mapper;

import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.model.User;

public class UserMapper {

    public static User mapDtoToUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setCountry(userDto.getCountry());
        user.setEmail(userDto.getEmail());
        user.setApproved(true);
        return user;
    }
}
