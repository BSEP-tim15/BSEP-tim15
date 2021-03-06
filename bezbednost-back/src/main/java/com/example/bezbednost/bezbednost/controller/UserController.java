package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.*;
import com.example.bezbednost.bezbednost.exception.InvalidInputException;
import com.example.bezbednost.bezbednost.iservice.IUserService;
import com.example.bezbednost.bezbednost.model.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UserController {

    private final IUserService userService;

    private Logger loggerInfo = LoggerFactory.getLogger(UserController.class);
    private Logger loggerError = LoggerFactory.getLogger("logerror");

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public User user(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-GU20 status=success ID=" + user.getId());
        return user;
    }

    @PostMapping()
    public ResponseEntity<User> registerUser(@RequestBody UserDto userDto){
        try {
            userService.save(userDto);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=UC-RU21 status=success");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (InvalidInputException e) {
            loggerError.error("location=UserController timestamp=" + LocalDateTime.now().toString() + " action=UC-RU21 status=failure message=" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/isUserRegistered")
    public boolean isUserRegistered(@RequestParam String username){
        loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=IS_USER_REGISTERED status=success");
        return userService.isUserRegistered(username);
    }

    @GetMapping("/getRole/{id}")
    public String getUserRole(@PathVariable Integer id) {
        try {
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_ROLE status=success");
            return this.userService.findUserRole(id);
        }  catch (Exception e) {
            loggerError.error("location=UserController timestamp=" + LocalDateTime.now().toString() + " action=GET_ROLE status=failure message=" + e.getMessage());
            return String.valueOf(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code) {
        boolean verified = userService.verify(code);
        loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-VA22 status=success");
        return verified ? "Your account is successfully verified!" : "We are sorry, your account is not verified.";
    }

    @PostMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestBody EmailDto emailDto){
        try {
            userService.resetPassword(emailDto.getEmail());
            loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-RP23 status=success");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=UserController timestamp=" + LocalDateTime.now().toString() + " action=UC-RP23 status=failure message=" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/validateToken")
    public ResponseEntity validateToken(@Param("token") String token){
        String result = userService.validatePasswordResetToken(token);
        if(result != null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-VT24 status=success");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PutMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestBody PasswordDto passwordDto){
        String result = userService.validatePasswordResetToken(passwordDto.getToken());
        if(result != null) {
            loggerError.error("location=UserController timestamp=" + LocalDateTime.now().toString() + " action=UC-RP23 status=failure message=Token is not valid");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            userService.resetPassword(passwordDto);
            loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-RP23 status=success");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PutMapping("/changePassword")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto, Principal user){
        User u = userService.findByUsername(user.getName());
        String result = userService.changePassword(u, changePasswordDto);
        if(result.equals("OK")) {
            loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-CP25 status=success ID=" + u.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            loggerError.error("location=UserController timestamp=" + LocalDateTime.now().toString() + " action=UC-CP25 status=failure");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/sendLoginEmail/{email}")
    public ResponseEntity sendLoginEmail(@PathVariable String email){
        userService.sendLoginEmail(email);
        loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-SLE26 status=success");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/validatePasswordToken")
    public ResponseEntity validatePasswordToken(@Param("token") String token){
        String result = userService.validatePasswordlessLoginToken(token);
        if (result != null) {
            loggerError.error("location=UserController timestamp=" + LocalDateTime.now().toString() + " action=UC-VPT27 status=failure message=Token is not valid");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-VPT27 status=success");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PutMapping("/twoFactor")
    public ResponseEntity changeTwoFactorAuth(@RequestBody EnableTwoFactorAuthDto enableTwoFactorAuthDto) {
        try {
            userService.changeUsingTwoFactorAuth(enableTwoFactorAuthDto.getIsEnabled(), enableTwoFactorAuthDto.getId());
            loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=UC-EDTFA28 status=success");
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=UserController timestamp=" + LocalDateTime.now().toString() + " action=UC-EDTFA28 status=failure message=Code is not valid");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/secretCode/{id}")
    public ResponseEntity<String> getSecretCode(@PathVariable Integer id) {
        try {
            loggerInfo.info("timestamp=" + LocalDateTime.now().toString() + " action=GET_CODE status=success");
            return new ResponseEntity<>(userService.getSecretCode(id), HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=UserController timestamp=" + LocalDateTime.now().toString() + " action=GET_CODE status=failure message=Code is not valid");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
