package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.CertificateDto;
import com.example.bezbednost.bezbednost.iservice.IKeyService;
import com.example.bezbednost.bezbednost.iservice.IPostCertificateService;
import com.example.bezbednost.bezbednost.iservice.IGetCertificateService;
import com.example.bezbednost.bezbednost.iservice.IRevocationService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Slf4j

public class CertificateController {
    private final IPostCertificateService postCertificateService;
    private final IGetCertificateService getCertificateService;
    private final IKeyService keyService;
    private final IRevocationService revocationService;

    public CertificateController(IPostCertificateService postCertificateService, IGetCertificateService getCertificateService, IKeyService keyService, IRevocationService revocationService) {
        this.postCertificateService = postCertificateService;
        this.getCertificateService = getCertificateService;
        this.keyService = keyService;
        Security.addProvider(new BouncyCastleProvider());
        this.revocationService = revocationService;
    }

    @PostMapping
    public ResponseEntity<CertificateDto> createCertificate(@RequestBody CertificateDto certificateDTO) throws
            CertificateException, OperatorCreationException, IOException, NoSuchAlgorithmException, KeyStoreException,
            SignatureException, InvalidKeyException, NoSuchProviderException, UnrecoverableKeyException {
        postCertificateService.createCertificate(certificateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CertificateDto>> getCertificates(@RequestParam String certificateType) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getCertificates(certificateType), HttpStatus.OK);
    }

    @GetMapping("/bySubject")
    public ResponseEntity<List<CertificateDto>> getCertificatesBySubject(@RequestParam String subject) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getCertificatesBySubject(subject), HttpStatus.OK);
    }

    @GetMapping("/issuers")
    public ResponseEntity<List<String>> getIssuers() throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getIssuers(), HttpStatus.OK);
    }

    @GetMapping("/maxDate")
    public ResponseEntity<Date> getMaxDateForCertificate(@RequestParam String issuer) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getMaxDateForCertificate(issuer), HttpStatus.OK);
    }

    @GetMapping("/certificate")
    public ResponseEntity<CertificateDto> getCertificate(@RequestParam BigInteger serialNumber) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getCertificateBySerialNumber(serialNumber), HttpStatus.OK);
    }

    @PostMapping("/exportCertificate")
    public ResponseEntity<BigInteger> exportCertificate(@RequestParam BigInteger serialNumber) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        postCertificateService.exportCertificate(serialNumber);
        return new ResponseEntity<>(serialNumber, HttpStatus.CREATED);
    }


    @GetMapping("/parentCertificateSerialNumber")
    public ResponseEntity<BigInteger> getSerialNumberOfParentCertificate(@RequestParam BigInteger serialNumber) throws
            CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getSerialNumberOfParentCertificate(serialNumber), HttpStatus.OK);
    }

}
