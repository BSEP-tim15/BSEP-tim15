package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.config.TokenUtils;
import com.example.bezbednost.bezbednost.config.UserTokenState;
import com.example.bezbednost.bezbednost.dto.JwtAuthenticationDto;
import com.example.bezbednost.bezbednost.dto.PasswordlessLoginDto;
import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.exception.InvalidInputException;
import com.example.bezbednost.bezbednost.exception.ResourceConflictException;
import com.example.bezbednost.bezbednost.iservice.IUserService;
import com.example.bezbednost.bezbednost.model.PasswordlessLoginToken;
import com.example.bezbednost.bezbednost.model.User;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;

    public AuthenticationController(TokenUtils tokenUtils, AuthenticationManager authenticationManager, IUserService userService) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody JwtAuthenticationDto authenticationRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("******************************************************");
        System.out.println(SecurityContextHolder.getContext().getAuthentication());

        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getUsername(), user.getRoleNames(), user.getPermissionNames());
        int expiresIn = tokenUtils.getExpiresIn();

        return ResponseEntity.ok(new UserTokenState(jwt, (long) expiresIn));
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto, UriComponentsBuilder ucBuilder) throws InvalidInputException {
        User existUser = this.userService.findByUsername(userDto.getUsername());
        User existEmail =  this.userService.findByEmail(userDto.getEmail());

        if (existUser != null) {
            throw new ResourceConflictException(userDto.getUsername(), "Username already exists!");
        } else if(existEmail != null) {
            throw new ResourceConflictException(userDto.getEmail(), "Email already exist!");
        }

        User user = this.userService.save(userDto);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/loginPasswordless")
    public ResponseEntity<UserTokenState> createAuthenticationTokenPasswordless(@Param("token") String token, HttpServletResponse response) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                token, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findUserFromToken(token);
        String jwt = tokenUtils.generateToken(user.getUsername(), user.getRoleNames(), user.getPermissionNames());
        int expiresIn = tokenUtils.getExpiresIn();

        return ResponseEntity.ok(new UserTokenState(jwt, (long) expiresIn));
    }

}
