package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IRoleService;
import com.example.bezbednost.bezbednost.model.Role;
import com.example.bezbednost.bezbednost.repository.IRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;

    public RoleService(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findByName(String role) {
        System.out.println(roleRepository.findAll());
        System.out.println(role);
        System.out.println(roleRepository.findByName(role));
        return roleRepository.findByName(role);
    }
}
