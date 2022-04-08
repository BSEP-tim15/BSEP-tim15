package com.example.bezbednost.bezbednost.dto;

import lombok.Getter;
import lombok.Setter;

public class FileDto {

    @Getter @Setter private String fileName;

    @Getter @Setter private String password;

    public FileDto() { }

    public FileDto(String fileName, String password) {
        this.fileName = fileName;
        this.password = password;
    }
}
