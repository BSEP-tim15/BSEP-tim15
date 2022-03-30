package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.CertificateDTO;
import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.exception.ResourceConflictException;
import com.example.bezbednost.bezbednost.iservice.IUserService;
import com.example.bezbednost.bezbednost.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Slf4j
public class CertificateController {

    private final IUserService userService;

    public CertificateController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CertificateDTO> createCertificate(@RequestBody CertificateDTO certificateDTO){
        /*User user = userService.findByUsername(certificateDTO.getSubject().getUsername());

        if (user != null) {
            throw new ResourceConflictException(user.getUsername(), "Username already exists");
        }*/

        userService.save(new UserDto(certificateDTO.getSubjectName(), certificateDTO.getSubjectUsername(),
                certificateDTO.getSubjectEmail(), certificateDTO.getSubjectCountry(), certificateDTO.getCertificateType()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
