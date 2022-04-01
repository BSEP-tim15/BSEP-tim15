package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.CertificateDto;
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
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public ResponseEntity<CertificateDTO> createCertificate(@RequestBody CertificateDTO certificateDTO) throws
            CertificateException, OperatorCreationException, IOException, NoSuchAlgorithmException, KeyStoreException {
        KeyPair keyPair = keyService.generateKeyPair();
        X509Certificate certificate = certificationService.createCertificate(keyPair, certificateDTO);
        saveCertificate(certificate, keyPair.getPrivate(), certificateDTO.getCertificateType());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private void saveCertificate(X509Certificate certificate, PrivateKey privateKey, String type) throws CertificateException,
            IOException, NoSuchAlgorithmException, KeyStoreException {
       String fileName = type+"Certificates.jsk";
       System.out.println("Unesite sifru za fajl:" + type + "Certificates.jsk");
       Scanner scanner = new Scanner(System.in);
       String password = scanner.nextLine();
       try {
           keyService.loadKeyStore(fileName, password.toCharArray());
        }
        catch(FileNotFoundException e){
            keyService.loadKeyStore(null, password.toCharArray());
            keyService.saveKeyStore(fileName, password.toCharArray());
        }
        keyService.writeToKeyStore(String.valueOf(certificate.getSerialNumber()), privateKey,
                password.toCharArray(), certificate);
        keyService.saveKeyStore(fileName, password.toCharArray());
    }

    @GetMapping
    public ResponseEntity<List<CertificateDTO>> getCertificates() throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDTO> rootCertificates = new ArrayList<>();
        List<CertificateDTO> intermediateCertificates = new ArrayList<>();
        List<CertificateDTO> entityCertificates = new ArrayList<>();
        try{
            rootCertificates = certificationService.getAllCertificates("rootCertificates.jsk",
                    "sifra".toCharArray());
        }catch(FileNotFoundException e){
        }
        try{
            intermediateCertificates = certificationService.getAllCertificates
                    ("intermediateCertificates.jsk","sifra".toCharArray());
        }catch(FileNotFoundException e){
        }
        try{
            entityCertificates = certificationService.getAllCertificates("end-entityCertificates.jsk",
                    "sifra".toCharArray());
        }catch(FileNotFoundException e){
        }
        var certificates = Stream.of(rootCertificates, intermediateCertificates, entityCertificates)
                .flatMap(Collection::stream).collect(Collectors.toList());

        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }

}
