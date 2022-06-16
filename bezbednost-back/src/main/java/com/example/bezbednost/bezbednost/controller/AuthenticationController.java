package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.config.TokenUtils;
import com.example.bezbednost.bezbednost.config.UserTokenState;
import com.example.bezbednost.bezbednost.dto.JwtAuthenticationDto;
import com.example.bezbednost.bezbednost.dto.TfaAuthenticationDto;
import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.exception.InvalidInputException;
import com.example.bezbednost.bezbednost.exception.ResourceConflictException;
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
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;

    private Logger loggerInfo = LoggerFactory.getLogger(AuthenticationController.class);
    private Logger loggerError = LoggerFactory.getLogger("logerror");

    @Autowired
    public AuthenticationController(TokenUtils tokenUtils, AuthenticationManager authenticationManager, IUserService userService) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody JwtAuthenticationDto authenticationRequest, HttpServletResponse response) {
        try {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getUsername(), user.getRoleNames(), user.getPermissionNames());
        int expiresIn = tokenUtils.getExpiresIn();
        UserTokenState userTokenState = new UserTokenState(jwt, (long) expiresIn);

        if (user.isUsing2FA()) {
            userTokenState.setAccessToken("2fa");
        }

        loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=LOGIN status=success ID="+ user.getId());
        return ResponseEntity.ok(userTokenState);

        } catch (Exception e) {
            loggerError.error("location=AuthenticationController timestamp="+LocalDateTime.now().toString()+" action=LOGIN status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/tfa-login")
    public ResponseEntity<UserTokenState> twoFactorAuth(@RequestBody TfaAuthenticationDto tfaAuthenticationDto) {
        try {
            UserTokenState userTokenState = userService.login2fa(tfaAuthenticationDto);
            if (userTokenState != null) {
                return new ResponseEntity<>(userTokenState, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto, UriComponentsBuilder ucBuilder) throws InvalidInputException {
        try {
            User existUser = this.userService.findByUsername(userDto.getUsername());
            User existEmail =  this.userService.findByEmail(userDto.getEmail());

            if (existUser != null) {
                throw new ResourceConflictException(userDto.getUsername(), "Username already exists!");
            } else if(existEmail != null) {
                throw new ResourceConflictException(userDto.getEmail(), "Email already exist!");
            }

            User user = this.userService.save(userDto);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=SIGNUP status=success ID="+ user.getId());

            return new ResponseEntity<>(user, HttpStatus.CREATED);

        } catch (Exception e) {
            loggerError.error("location=AuthenticationController timestamp="+LocalDateTime.now().toString()+" action=SIGNUP status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/loginPasswordless")
    public ResponseEntity<UserTokenState> createAuthenticationTokenPasswordless(@Param("token") String token, HttpServletResponse response) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    token, null);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findUserFromToken(token);
            String jwt = tokenUtils.generateToken(user.getUsername(), user.getRoleNames(), user.getPermissionNames());
            int expiresIn = tokenUtils.getExpiresIn();

            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=PASSWORDLESS_LOGIN status=success ID="+ user.getId());

            return ResponseEntity.ok(new UserTokenState(jwt, (long) expiresIn));

        } catch (Exception e) {
            loggerError.error("location=AuthenticationController timestamp="+LocalDateTime.now().toString()+" action=PASSWORDLESS_LOGIN status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
