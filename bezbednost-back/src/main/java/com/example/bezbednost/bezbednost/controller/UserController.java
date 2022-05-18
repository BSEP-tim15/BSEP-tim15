package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.ChangePasswordDto;
import com.example.bezbednost.bezbednost.dto.EmailDto;
import com.example.bezbednost.bezbednost.dto.PasswordDto;
import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.iservice.IUserService;
import com.example.bezbednost.bezbednost.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@AllArgsConstructor
public class UserController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping()
    public User user(Principal user) {
        return userService.findByUsername(user.getName());
    }

    @PostMapping()
    public ResponseEntity<User> registerUser(@RequestBody UserDto userDto){
        userService.save(userDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/isUserRegistered")
    public boolean isUserRegistered(@RequestParam String username){
        return userService.isUserRegistered(username);
    }

    @GetMapping("/getRole/{id}")
    public String getUserRole(@PathVariable Integer id) { return this.userService.findUserRole(id); }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code) {
        boolean verified = userService.verify(code);

        return verified ? "Your account is successfully verified!" : "We are sorry, your account is not verified.";
    }

    @PostMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestBody EmailDto emailDto){
        userService.resetPassword(emailDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/validateToken")
    public ResponseEntity validateToken(@Param("token") String token){
        String result = userService.validatePasswordResetToken(token);
        if(result != null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PutMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestBody PasswordDto passwordDto){
        String result = userService.validatePasswordResetToken(passwordDto.getToken());
        if(result != null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            userService.resetPassword(passwordDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PutMapping("/changePassword")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto, Principal user){
        User u = userService.findByUsername(user.getName());
        String result = userService.changePassword(u, changePasswordDto);
        if(result == "OK")
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/sendLoginEmail/{email}")
    public ResponseEntity sendLoginEmail(@PathVariable String email){
        userService.sendLoginEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
