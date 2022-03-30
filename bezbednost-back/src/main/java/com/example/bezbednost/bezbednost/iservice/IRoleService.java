package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.model.Role;

import java.util.List;

public interface IRoleService {
    List<Role> findByName(String role);
}
