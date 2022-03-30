package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.CertificateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Slf4j
public class CertificateController {

    @PostMapping
    public ResponseEntity<CertificateDTO> addLodge(@RequestBody CertificateDTO certificateDTO){
        System.out.println(certificateDTO.getIssuer());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
