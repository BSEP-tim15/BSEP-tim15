package com.example.bezbednost.bezbednost.dto;

import lombok.Getter;
import lombok.Setter;

public class JwtAuthenticationDto {

    @Getter @Setter private String username;
    @Getter @Setter private String password;

    public JwtAuthenticationDto() { }

    public JwtAuthenticationDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
