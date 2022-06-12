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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Slf4j
public class CertificateController {
    private final IPostCertificateService postCertificateService;
    private final IGetCertificateService getCertificateService;
    private final IRevocationService revocationService;

    private Logger loggerInfo = LoggerFactory.getLogger(CertificateController.class);
    private Logger loggerError = LoggerFactory.getLogger("logerror");


    @Autowired
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
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=CREATE_CERTIFICATE status=success serial_number="+ certificateDTO.getSerialNumber());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (InvalidInputException e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=CREATE_CERTIFICATE status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/certificates")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<List<CertificateDto>> getCertificates(@RequestBody GetCertificateDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_CERTIFICATES status=success");
        return new ResponseEntity<>(getCertificateService.getCertificates(certificate), HttpStatus.OK);
    }

    @PostMapping("/bySubject")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<List<CertificateDto>> getCertificatesBySubject(@RequestBody GetCertificateBySomeoneDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        try {
            List<CertificateDto> certificates = getCertificateService.getCertificatesBySubject(certificate);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_CERTIFICATES_BY_SUBJECT status=success");
            return new ResponseEntity<>(certificates, HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=GET_CERTIFICATES_BY_SUBJECT status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/issuers")
    @PreAuthorize("hasAuthority('get_issuers')")
    public ResponseEntity<List<String>> getIssuers(@RequestBody PasswordsDto passwords) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableKeyException, OCSPException, OperatorCreationException {
        try {
            List<String> issuers = getCertificateService.getIssuers(passwords);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_ISSUERS status=success");
            return new ResponseEntity<>(issuers, HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=GET_ISSUERS status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/maxDate")
    public ResponseEntity<Date> getMaxDateForCertificate(@RequestBody GetCertificateBySomeoneDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        try {
            Date maxDate = getCertificateService.getMaxDateForCertificate(certificate);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_MAX_DATE status=success");
            return new ResponseEntity<>(maxDate, HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=GET_MAX_DATE status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/singleCertificate")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<CertificateDto> getCertificate(@RequestBody GetSingleCertificateDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        try {
            CertificateDto certificateDto = getCertificateService.getCertificateBySerialNumber(certificate);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_CERTIFICATE status=success");
            return new ResponseEntity<>(certificateDto, HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=GET_CERTIFICATE status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/exportCertificate")
    public ResponseEntity<GetSingleCertificateDto> exportCertificate(@RequestBody GetSingleCertificateDto certificate) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        try {
            postCertificateService.exportCertificate(certificate);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=EXPORT_CERTIFICATE status=success serial_number=" + certificate.getSerialNumber());
            return new ResponseEntity<>(certificate, HttpStatus.CREATED);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=EXPORT_CERTIFICATE status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/parentCertificateSerialNumber")
    @PreAuthorize("hasAuthority('read_certificate')")
    public ResponseEntity<BigInteger> getSerialNumberOfParentCertificate(@RequestBody GetSingleCertificateDto certificate) throws
            CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            BigInteger parentSerialNumber = getCertificateService.getSerialNumberOfParentCertificate(certificate);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_PARENT_SERIAL_NUMBER status=success serial_number=" + certificate.getSerialNumber());
            return new ResponseEntity<>(parentSerialNumber, HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=GET_PARENT_SERIAL_NUMBER status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCertificate(@RequestBody GetSingleCertificateDto certificateDto) {
        try {
            boolean isValid = revocationService.checkIfCertificateIsValid(certificateDto);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=VALIDATE_CERTIFICATE status=success serial_number=" + certificateDto.getSerialNumber());
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=VALIDATE_CERTIFICATE status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/canUserCreateCertificate/{username}")
    public ResponseEntity<?> canUserCreateCertificate(@RequestBody GetCertificateDto certificateDto, @PathVariable String username) {
        try {
            boolean isValid = getCertificateService.canUserCreateCertificate(certificateDto, username);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=CAN_USER_CREATE_CERTIFICATE status=success");
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=CAN_USER_CREATE_CERTIFICATE status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasAuthority('revoke_certificate')")
    public ResponseEntity<?> revokeCertificate(@RequestBody SerialNumberDto serialNumberDto) {
        try {
            revocationService.revokeCertificate(serialNumberDto.getSerialNumber());
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=REVOKE_CERTIFICATE status=success");
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=REVOKE_CERTIFICATE status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/revoked")
    public ResponseEntity<?> isRevoked(@RequestBody GetSingleCertificateDto certificateDto) {
        try {
            boolean isRevoked = revocationService.checkIfCertificateIsRevoked(certificateDto);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=IS_CERTIFICATE_REVOKED status=success");
            return ResponseEntity.ok(isRevoked);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=IS_CERTIFICATE_REVOKED status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("intermediateCertificates/{username}")
    public ResponseEntity<List<CertificateDto>> getCertificatesForIntermediate(@RequestBody GetCertificateDto certificate, @PathVariable String username) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            List<CertificateDto> certificates = getCertificateService.getCertificatesForIntermediate(certificate, username);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_INTERMEDIATE_CERTIFICATES status=success");
            return new ResponseEntity<>(certificates, HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=GET_INTERMEDIATE_CERTIFICATES status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("endEntityCertificates/{username}")
    public ResponseEntity<List<CertificateDto>> getCertificatesForEndEntity(@RequestBody GetCertificateDto certificate, @PathVariable String username) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            List<CertificateDto> certificates = getCertificateService.getCertificatesForEndEntity(certificate, username);
            loggerInfo.info("timestamp="+ LocalDateTime.now().toString()+" action=GET_END_ENTITY_CERTIFICATES status=success");
            return new ResponseEntity<>(certificates, HttpStatus.OK);
        } catch (Exception e) {
            loggerError.error("location=CertificateController timestamp="+LocalDateTime.now().toString()+" action=GET_END_ENTITY_CERTIFICATES status=failure message="+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
