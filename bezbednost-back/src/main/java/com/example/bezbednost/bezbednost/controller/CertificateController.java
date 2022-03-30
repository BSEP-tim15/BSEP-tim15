package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.CertificateDTO;
import com.example.bezbednost.bezbednost.iservice.ICertificationService;
import com.example.bezbednost.bezbednost.iservice.IKeyService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Slf4j

public class CertificateController {
    private final ICertificationService certificationService;
    private final IKeyService keyService;

    public CertificateController(ICertificationService certificationService, IKeyService keyService) {
        this.certificationService = certificationService;
        this.keyService = keyService;
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostMapping
    public ResponseEntity<CertificateDTO> createCertificate(@RequestBody CertificateDTO certificateDTO) throws CertificateException,
            OperatorCreationException, IOException, NoSuchAlgorithmException, KeyStoreException {
        System.out.println(certificateDTO.getIssuer());
        KeyPair keyPair = keyService.generateKeyPair();
        X509Certificate certificate = certificationService.createCertificate(keyPair, certificateDTO);

        System.out.println("\n===== Sertifikat =====");
        System.out.println("-------------------------------------------------------");
        System.out.println(certificate);
        System.out.println("-------------------------------------------------------");
        keyService.loadKeyStore("proba.jks", "sifra".toCharArray());
        //dodati try catch
        keyService.writeToKeyStore(String.valueOf(certificate.getSerialNumber()), keyPair.getPrivate(),
                "sifra".toCharArray(), certificate);
        keyService.saveKeyStore("proba.jks", "sifra".toCharArray());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @JsonIgnoreProperties("publicKey")
    public ResponseEntity<List<CertificateDTO>> getCertificates() throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        var certificates = certificationService.getAllCertificates("proba.jks", "sifra".toCharArray());
        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }

}
