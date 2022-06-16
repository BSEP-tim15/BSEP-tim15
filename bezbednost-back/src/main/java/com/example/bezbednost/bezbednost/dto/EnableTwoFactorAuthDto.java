package com.example.bezbednost.bezbednost.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnableTwoFactorAuthDto {

    private Integer id;
    private Boolean isEnabled;
}
