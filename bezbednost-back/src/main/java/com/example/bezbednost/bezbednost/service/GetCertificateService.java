package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.CertificateDto;
import com.example.bezbednost.bezbednost.iservice.IGetCertificateService;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GetCertificateService implements IGetCertificateService {

    @Override
    public List<CertificateDto> getCertificates(String certificateType) throws KeyStoreException,
            NoSuchProviderException, IOException, CertificateException, NoSuchAlgorithmException {
        String fileName = getFileName(certificateType);
        if(fileName.equals("all")){
            return getAllCertificates();
        }
        else {
            return getCertificatesFromFile(fileName);
        }
    }

    private List<CertificateDto> getAllCertificates() throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException {
        String rootFile = "rootCertificates.jsk";
        String intermediateFile = "intermediateCertificates.jsk";
        String endEntityFile = "end-entityCertificates.jsk";

        return Stream.of(getCertificatesFromFile(rootFile), getCertificatesFromFile(intermediateFile), getCertificatesFromFile(endEntityFile))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private String getFileName(String certificateType){
        if(Objects.equals(certificateType, "root")){
            return "rootCertificates.jsk";
        }
        else if(Objects.equals(certificateType, "intermediate")){
            return "intermediateCertificates.jsk";
        }
        else if(Objects.equals(certificateType, "end-entity")){
            return "end-entityCertificates.jsk";
        }
        else {
            return "all";
        }
    }

    private List<CertificateDto> getCertificatesFromFile(String fileName) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> certificates = new ArrayList<>();
        try{
            //char[] password = getFilePassword(fileName);
            char[] password = "sifra".toCharArray();
            KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
            keyStore.load(new FileInputStream(fileName), password);
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                X509Certificate cert  = (X509Certificate) keyStore.getCertificate(alias);
                CertificateDto certificateDTO = new CertificateDto(cert.getSerialNumber(), cert.getIssuerDN().toString(),
                        cert.getSubjectDN().toString(), cert.getNotBefore(), cert.getNotAfter());
                certificateDTO.setCertificateType(getCertificateType(fileName));
                certificates.add(certificateDTO);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return certificates;
    }

    private char[] getFilePassword(String file){
        System.out.println("Please enter file password (" + file + ") :");
        Scanner scanner = new Scanner(System.in);
        String password = scanner.nextLine();
        return password.toCharArray();
    }

    private String getCertificateType(String fileName) {
        if(fileName.contains("root")) {
            return "root";
        }
        else if(fileName.contains("intermediate")) {
            return "intermediate";
        }
        else {
            return "end-entity";
        }
    }

    @Override
    public List<String> getIssuers() throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException {
        List<String> issuers = new ArrayList<>();
        for(CertificateDto certificate : getAllCertificates()){
            String issuer = certificate.getIssuer().replace("CN=", "");
            if(!isIssuerAdded(issuers, issuer))
                issuers.add(issuer);
        }
        issuers.addAll(getIntermediateCertificatesSubjects());

        return issuers;
    }

    private List<String> getIntermediateCertificatesSubjects() throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException {
        List<String> issuers = new ArrayList<>();
        for(CertificateDto certificate : getAllCertificates()){
            String issuer = certificate.getSubject().replace("CN=", "");
            if(Objects.equals(certificate.getCertificateType(), "intermediate") && !isIssuerAdded(issuers, issuer))
                issuers.add(issuer);
        }

        return issuers;
    }

    private boolean isIssuerAdded(List<String> issuers, String issuer){
        return issuers.contains(issuer);
    }

    @Override
    public List<CertificateDto> getCertificatesBySubject(String subject) throws CertificateException, IOException,
            NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> subjectCertificates = new ArrayList<>();
        for(CertificateDto certificate : getAllCertificates()){
            String subjectUsername = "CN=" + subject;
            if(Objects.equals(subjectUsername, certificate.getSubject())){
                subjectCertificates.add(certificate);
            }
        }

        return subjectCertificates;
    }

    @Override
    public Date getMaxDateForCertificate(String issuer) throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException {
        List<CertificateDto> issuerCertificates = getCertificatesBySubject(issuer);
        Date maxDate = issuerCertificates.get(0).getValidTo();
        for (CertificateDto certificate : issuerCertificates) {
            if(certificate.getValidTo().before(maxDate)){
                maxDate = certificate.getValidTo();
            }
        }

        return maxDate;
    }

    @Override
    public CertificateDto getCertificateBySerialNumber(BigInteger serialNumber) throws CertificateException, IOException,
            NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        for(CertificateDto certificate : getAllCertificates()){
            if(Objects.equals(certificate.getSerialNumber(), serialNumber)){
                return certificate;
            }
        }

        return null;
    }

}
