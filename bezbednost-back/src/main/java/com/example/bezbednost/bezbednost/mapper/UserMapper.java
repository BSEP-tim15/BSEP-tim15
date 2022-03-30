package com.example.bezbednost.bezbednost.mapper;

import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.model.User;

public class UserMapper {

    public static User mapDtoToUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setUsername(userDto.getUsername());
        user.setApproved(true);
        return user;
    }
}
