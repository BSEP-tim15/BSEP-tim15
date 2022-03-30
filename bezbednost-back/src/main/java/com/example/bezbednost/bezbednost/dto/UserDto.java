package com.example.bezbednost.bezbednost.dto;

import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter @Setter private String name;
    @Getter @Setter private String surname;
    @Getter @Setter private String username;
    @Getter @Setter private String password;
    @Getter @Setter private String role;
}
