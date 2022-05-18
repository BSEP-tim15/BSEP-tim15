package com.example.bezbednost.bezbednost.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordlessLoginDto {
    private String email;
}
