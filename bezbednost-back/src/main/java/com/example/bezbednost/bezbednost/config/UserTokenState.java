package com.example.bezbednost.bezbednost.config;

import lombok.Getter;
import lombok.Setter;

public class UserTokenState {
    @Getter @Setter private String accessToken;
    @Getter @Setter private Long expiresIn;

    public UserTokenState() {
        this.accessToken = null;
        this.expiresIn = null;
    }

    public UserTokenState(String accessToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
