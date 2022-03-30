package com.example.bezbednost.bezbednost.exception;

import lombok.Getter;
import lombok.Setter;

public class ResourceConflictException extends RuntimeException {
    private static final long serialVersionUID = 1791564636123821405L;

    @Getter @Setter private String resourceUsername;

    public ResourceConflictException(String username, String message) {
        super(message);
        this.setResourceUsername(username);
    }
}
