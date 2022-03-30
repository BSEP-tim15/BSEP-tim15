package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.iservice.IRoleService;
import com.example.bezbednost.bezbednost.iservice.IUserService;
import com.example.bezbednost.bezbednost.mapper.UserMapper;
import com.example.bezbednost.bezbednost.model.Role;
import com.example.bezbednost.bezbednost.model.User;
import com.example.bezbednost.bezbednost.repository.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;

    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder, IRoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(UserDto userDto) {
        User user = UserMapper.mapDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        List<Role> roles = roleService.findByName(userDto.getRole());
        user.setRoles(roles);
        userRepository.save(user);

        return user;
    }
}
