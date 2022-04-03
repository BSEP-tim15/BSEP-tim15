package com.example.bezbednost.bezbednost.controller;

import com.example.bezbednost.bezbednost.dto.CertificateDto;
import com.example.bezbednost.bezbednost.iservice.ICertificationService;
import com.example.bezbednost.bezbednost.iservice.IKeyService;
import com.example.bezbednost.bezbednost.iservice.IKeyToolService;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Slf4j

public class CertificateController {
    private final ICertificationService certificationService;
    private final IKeyService keyService;
    private final IKeyToolService keyToolService;

    public CertificateController(ICertificationService certificationService, IKeyService keyService, IKeyToolService keyToolService) {
        this.certificationService = certificationService;
        this.keyService = keyService;
        this.keyToolService = keyToolService;
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostMapping
    public ResponseEntity<CertificateDto> createCertificate(@RequestBody CertificateDto certificateDTO) throws
            CertificateException, OperatorCreationException, IOException, NoSuchAlgorithmException, KeyStoreException {
        KeyPair keyPair = keyService.generateKeyPair();
        X509Certificate certificate = certificationService.createCertificate(keyPair, certificateDTO);
        saveCertificate(certificate, keyPair.getPrivate(), certificateDTO.getCertificateType());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private void saveCertificate(X509Certificate certificate, PrivateKey privateKey, String type) throws CertificateException,
            IOException, NoSuchAlgorithmException, KeyStoreException {
        String fileName = type+"Certificates.jsk";
        System.out.println("Please enter file password:" + type + "Certificates.jsk");
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
    public ResponseEntity<List<CertificateDto>> getCertificates(@RequestParam String certificateType) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        if(Objects.equals(certificateType, "root")){
            return new ResponseEntity<>(getRootCertificates(), HttpStatus.OK);
        }
        else if(Objects.equals(certificateType, "intermediate")){
            return new ResponseEntity<>(getIntermediateCertificates(), HttpStatus.OK);
        }
        else if(Objects.equals(certificateType, "end-entity")){
            return new ResponseEntity<>(getEndEntityCertificates(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(getAllCertificates(), HttpStatus.OK);
        }
    }

    private List<CertificateDto> getAllCertificates() throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException {
        return Stream.of(getRootCertificates(), getIntermediateCertificates(), getEndEntityCertificates())
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<CertificateDto> getRootCertificates() throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException {
        List<CertificateDto> rootCertificates = new ArrayList<>();
        try{
            System.out.println("Please enter file password (rootCertificates.jsk):");
            //Scanner scanner = new Scanner(System.in);
            //String password = scanner.nextLine();
            String password = "sifra";
            rootCertificates = certificationService.getCertificates("rootCertificates.jsk",
                    password.toCharArray());
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return rootCertificates;
    }

    private List<CertificateDto> getIntermediateCertificates() throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> intermediateCertificates = new ArrayList<>();
        try{
            System.out.println("Please enter file password (intermediateCertificates.jsk):");
            //Scanner scanner = new Scanner(System.in);
            //String password = scanner.nextLine();
            String password = "sifra";
            intermediateCertificates = certificationService.getCertificates("intermediateCertificates.jsk",
                    password.toCharArray());
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return intermediateCertificates;
    }

    private List<CertificateDto> getEndEntityCertificates() throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> endEntityCertificates = new ArrayList<>();
        try{
            System.out.println("Please enter file password (end-entityCertificates.jsk):");
            //Scanner scanner = new Scanner(System.in);
            //String password = scanner.nextLine();
            String password = "sifra";
            endEntityCertificates = certificationService.getCertificates("end-entityCertificates.jsk",
                   password.toCharArray());
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return endEntityCertificates;
    }

    @GetMapping("/bySubject")
    public ResponseEntity<List<CertificateDto>> getCertificatesBySubject(@RequestParam String subject) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> subjectCertificates = certificationService.getCertificatesBySubject(getAllCertificates(), subject);
        return new ResponseEntity<>(subjectCertificates, HttpStatus.OK);
    }

    @GetMapping("/issuers")
    public ResponseEntity<List<String>> getIssuers() throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> certificates = getAllCertificates();
        return new ResponseEntity<>(certificationService.getIssuers(certificates), HttpStatus.OK);
    }

    @GetMapping("/maxDate")
    public ResponseEntity<Date> getMaxDateForCertificate(@RequestParam String issuer) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> issuerCertificates = certificationService.getCertificatesBySubject(getAllCertificates(), issuer);
        return new ResponseEntity<>(certificationService.getMaxDateForCertificate(issuerCertificates), HttpStatus.OK);
    }

    @GetMapping("/certificate")
    public ResponseEntity<CertificateDto> getCertificate(@RequestParam BigInteger serialNumber) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        return new ResponseEntity<>(certificationService.getCertificateBySerialNumber(getAllCertificates(), serialNumber), HttpStatus.OK);
    }

    @PostMapping("/exportCertificate")
    public void exportCertificate(@RequestParam BigInteger serialNumber) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        CertificateDto certificate = certificationService.getCertificateBySerialNumber(getAllCertificates(), serialNumber);
        String keyStorePassword = "sifra";
        String certificateName = serialNumber.toString();
        String keyStoreName = certificate.getCertificateType();
        String command = "-exportcert -alias " + serialNumber + " -storepass " + keyStorePassword + " -file " +
                certificateName + ".cer -keystore " + keyStoreName +  "Certificates.jsk";
        keyToolService.executeCommand(command);
    }
}
