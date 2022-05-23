package com.example.bezbednost.bezbednost.repository;

import com.example.bezbednost.bezbednost.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IRoleRepository extends JpaRepository<Role, Integer> {

    @Query("select r from Role r where r.name=?1")
    List<Role> findByName(String role);
}
