package com.example.bezbednost.bezbednost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
public class BezbednostApplication {

    public static void main(String[] args) {
        SpringApplication.run(BezbednostApplication.class, args);
    }

}
