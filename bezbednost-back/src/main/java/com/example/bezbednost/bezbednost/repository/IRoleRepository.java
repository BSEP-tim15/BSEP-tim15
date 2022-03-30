package com.example.bezbednost.bezbednost.repository;

import com.example.bezbednost.bezbednost.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRoleRepository extends JpaRepository<Role, Integer> {

    List<Role> findByName(String role);
}
