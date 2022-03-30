package com.example.bezbednost.bezbednost.dto;

import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter @Setter private String name;
    @Getter @Setter private String username;
    @Getter @Setter private String country;
    @Getter @Setter private String email;
    @Getter @Setter private String role;

    public UserDto(String name, String username, String country, String email, String role) {
        this.name = name;
        this.username = username;
        this.country = country;
        this.email = email;
        this.role = role;
    }
}
