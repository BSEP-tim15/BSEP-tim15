package com.example.bezbednost.bezbednost.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String name;
    private String username;
    private String password;
    private String country;
    private String email;
    private String role;

}
