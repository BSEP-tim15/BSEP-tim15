package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.CertificateDTO;
import com.example.bezbednost.bezbednost.iservice.ICertificationService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Slf4j

public class CertificateController {
    private final ICertificationService certificationService;

    public CertificateController(ICertificationService certificationService) {
        this.certificationService = certificationService;
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostMapping
    public ResponseEntity<CertificateDTO> createCertificate(@RequestBody CertificateDTO certificateDTO) throws CertificateException,
            OperatorCreationException {
        System.out.println(certificateDTO.getIssuer());
        X509Certificate certificate = certificationService.createCertificate(certificateDTO);
        System.out.println("\n===== Sertifikat =====");
        System.out.println("-------------------------------------------------------");
        System.out.println(certificate);
        System.out.println("-------------------------------------------------------");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
