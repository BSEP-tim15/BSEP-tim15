package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.PasswordsDto;
import com.example.bezbednost.bezbednost.dto.certificate.CertificateDto;
import com.example.bezbednost.bezbednost.dto.SerialNumberDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetCertificateBySomeoneDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetCertificateDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetSingleCertificateDto;
import com.example.bezbednost.bezbednost.exception.InvalidInputException;
import com.example.bezbednost.bezbednost.iservice.IKeyService;
import com.example.bezbednost.bezbednost.iservice.IPostCertificateService;
import com.example.bezbednost.bezbednost.iservice.IGetCertificateService;
import com.example.bezbednost.bezbednost.iservice.IRevocationService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Slf4j

public class CertificateController {
    private final IPostCertificateService postCertificateService;
    private final IGetCertificateService getCertificateService;
    private final IRevocationService revocationService;

    public CertificateController(IPostCertificateService postCertificateService, IGetCertificateService getCertificateService, IRevocationService revocationService) {
        this.postCertificateService = postCertificateService;
        this.getCertificateService = getCertificateService;
        Security.addProvider(new BouncyCastleProvider());
        this.revocationService = revocationService;
    }

    @PostMapping("/createCertificate")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<CertificateDto> createCertificate(@RequestBody CertificateDto certificateDTO) throws
            CertificateException, OperatorCreationException, IOException, NoSuchAlgorithmException, KeyStoreException,
            SignatureException, InvalidKeyException, NoSuchProviderException, UnrecoverableKeyException {
        try {
            postCertificateService.createCertificate(certificateDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/certificates")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<List<CertificateDto>> getCertificates(@RequestBody GetCertificateDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getCertificates(certificate), HttpStatus.OK);
    }

    @PostMapping("/bySubject")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<List<CertificateDto>> getCertificatesBySubject(@RequestBody GetCertificateBySomeoneDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getCertificatesBySubject(certificate), HttpStatus.OK);
    }

    @PostMapping("/issuers")
    @PreAuthorize("hasAuthority('get_issuers')")
    public ResponseEntity<List<String>> getIssuers(@RequestBody PasswordsDto passwords) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableKeyException, OCSPException, OperatorCreationException {
        return new ResponseEntity<>(getCertificateService.getIssuers(passwords), HttpStatus.OK);
    }

    @PostMapping("/maxDate")
    public ResponseEntity<Date> getMaxDateForCertificate(@RequestBody GetCertificateBySomeoneDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getMaxDateForCertificate(certificate), HttpStatus.OK);
    }

    @PostMapping("/singleCertificate")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<CertificateDto> getCertificate(@RequestBody GetSingleCertificateDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getCertificateBySerialNumber(certificate), HttpStatus.OK);
    }

    @PostMapping("/exportCertificate")
    public ResponseEntity<GetSingleCertificateDto> exportCertificate(@RequestBody GetSingleCertificateDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        postCertificateService.exportCertificate(certificate);
        return new ResponseEntity<>(certificate, HttpStatus.CREATED);
    }

    @PostMapping("/parentCertificateSerialNumber")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<BigInteger> getSerialNumberOfParentCertificate(@RequestBody GetSingleCertificateDto certificate) throws
            CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getSerialNumberOfParentCertificate(certificate), HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCertificate(@RequestBody GetSingleCertificateDto certificateDto) {
        try {
            boolean isValid = revocationService.checkIfCertificateIsValid(certificateDto);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/canUserCreateCertificate/{username}")
    public ResponseEntity<?> canUserCreateCertificate(@RequestBody GetCertificateDto certificateDto, @PathVariable String username) {
        try {
            boolean isValid = getCertificateService.canUserCreateCertificate(certificateDto, username);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasAuthority('revoke_certificate')")
    public ResponseEntity<?> revokeCertificate(@RequestBody SerialNumberDto serialNumberDto) {
        try {
            revocationService.revokeCertificate(serialNumberDto.getSerialNumber());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/revoked")
    public ResponseEntity<?> isRevoked(@RequestBody GetSingleCertificateDto certificateDto) {
        try {
            boolean isRevoked = revocationService.checkIfCertificateIsRevoked(certificateDto);
            return ResponseEntity.ok(isRevoked);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("intermediateCertificates/{username}")
    public ResponseEntity<List<CertificateDto>> getCertificatesForIntermediate(@RequestBody GetCertificateDto certificate, @PathVariable String username) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getCertificatesForIntermediate(certificate, username), HttpStatus.OK);
    }

    @PostMapping("endEntityCertificates/{username}")
    public ResponseEntity<List<CertificateDto>> getCertificatesForEndEntity(@RequestBody GetCertificateDto certificate, @PathVariable String username) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        return new ResponseEntity<>(getCertificateService.getCertificatesForEndEntity(certificate, username), HttpStatus.OK);
    }

}
