package com.example.bezbednost.bezbednost.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class PasswordsDto {

    @Getter @Setter private String rootPassword;

    @Getter @Setter private String intermediatePassword;

    @Getter @Setter private String endEntityPassword;


    public PasswordsDto() { }

    public PasswordsDto(String rootPassword, String intermediatePassword, String endEntityPassword) {
        this.rootPassword = rootPassword;
        this.intermediatePassword = intermediatePassword;
        this.endEntityPassword = endEntityPassword;
    }
}
