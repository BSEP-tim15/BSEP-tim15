package com.example.bezbednost.bezbednost.repository;

import com.example.bezbednost.bezbednost.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.username=?1")
    User findByUsername(String username);
}
