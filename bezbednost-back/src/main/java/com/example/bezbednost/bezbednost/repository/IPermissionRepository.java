package com.example.bezbednost.bezbednost.repository;

import com.example.bezbednost.bezbednost.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPermissionRepository extends JpaRepository<Permission, Integer> {
}
