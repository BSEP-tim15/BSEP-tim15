package com.example.bezbednost.bezbednost.repository;


import com.example.bezbednost.bezbednost.model.PasswordlessLoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPasswordlessLoginTokenRepository extends JpaRepository<PasswordlessLoginToken, Long> {
    PasswordlessLoginToken findByToken(String token);
}
