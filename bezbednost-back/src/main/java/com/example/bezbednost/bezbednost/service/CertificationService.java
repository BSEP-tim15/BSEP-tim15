package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.CertificateDto;
import com.example.bezbednost.bezbednost.iservice.ICertificationService;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


@Service
public class CertificationService implements ICertificationService {
    private final KeyService keyService = new KeyService();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public X509Certificate createCertificate(KeyPair keyPair, CertificateDto certificateDTO) throws OperatorCreationException,
            CertificateException {
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC");
        ContentSigner contentSigner = builder.build(keyPair.getPrivate());
        X509v3CertificateBuilder certificateBuilder = generateCertificateBuilder(keyPair.getPublic(), certificateDTO);
        X509CertificateHolder certHolder = certificateBuilder.build(contentSigner);
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider("BC");
        return certConverter.getCertificate(certHolder);
    }

    private X509v3CertificateBuilder generateCertificateBuilder(PublicKey publicKey, CertificateDto certificateDTO){
        X500NameBuilder issuer = new X500NameBuilder(BCStyle.INSTANCE);
        issuer.addRDN(BCStyle.CN, certificateDTO.getIssuer());
        X500NameBuilder subject = new X500NameBuilder(BCStyle.INSTANCE);
        subject.addRDN(BCStyle.CN, certificateDTO.getSubject());
        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer.build(),
                createSerialNumber(),
                certificateDTO.getValidFrom(),
                certificateDTO.getValidTo(),
                subject.build(),
                publicKey);

        return builder;
    }

    private BigInteger createSerialNumber(){
        BigInteger maxLimit = new BigInteger("5000000000000");
        BigInteger minLimit = new BigInteger("25000000000");
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        Random randNum = new Random();
        int len = maxLimit.bitLength();
        BigInteger res = new BigInteger(len, randNum);
        if (res.compareTo(minLimit) < 0)
            res = res.add(minLimit);
        if (res.compareTo(bigInteger) >= 0)
            res = res.mod(bigInteger).add(minLimit);
        return res;
    }

    @Override
    public List<CertificateDto> getCertificates(String fileName, char[] password) throws KeyStoreException,
            NoSuchProviderException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
        keyStore.load(new FileInputStream(fileName), password);
        Enumeration<String> aliases = keyStore.aliases();
        List<CertificateDto> entries = new ArrayList<>();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            X509Certificate cert  = (X509Certificate) keyStore.getCertificate(alias);
            CertificateDto certificateDTO = new CertificateDto(cert.getSerialNumber(), cert.getIssuerDN().toString(),
                    cert.getSubjectDN().toString(), cert.getNotBefore(), cert.getNotAfter());
            certificateDTO.setCertificateType(getCertificateType(fileName));
            entries.add(certificateDTO);
        }
        return entries;
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
    public List<String> getIssuers(List<CertificateDto> certificates) {
        List<String> issuers = new ArrayList<>();
        for(CertificateDto certificate : certificates){
            String issuer = certificate.getIssuer().replace("CN=", "");
            if(!isIssuerAdded(issuers, issuer))
                issuers.add(issuer);
        }
        issuers.addAll(getIntermediateCertificatesSubjects(certificates));

        return issuers;
    }

    private List<String> getIntermediateCertificatesSubjects(List<CertificateDto> certificates){
        List<String> issuers = new ArrayList<>();
        for(CertificateDto certificate : certificates){
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
    public List<CertificateDto> getCertificatesBySubject(List<CertificateDto> allCertificates, String subject) {
        List<CertificateDto> subjectCertificates = new ArrayList<>();
        for(CertificateDto certificate : allCertificates){
            String subjectUsername = "CN=" + subject;
            if(Objects.equals(subjectUsername, certificate.getSubject())){
                subjectCertificates.add(certificate);
            }
        }

        return subjectCertificates;
    }

    @Override
    public Date getMaxDateForCertificate(List<CertificateDto> issuerCertificates){
        Date maxDate = issuerCertificates.get(0).getValidTo();
        for (CertificateDto certificate : issuerCertificates) {
            if(certificate.getValidTo().before(maxDate)){
                maxDate = certificate.getValidTo();
            }
        }

        return maxDate;
    }

    @Override
    public CertificateDto getCertificateBySerialNumber(List<CertificateDto> allCertificates, BigInteger alias) {
        for(CertificateDto certificate : allCertificates){
            if(Objects.equals(certificate.getSerialNumber(), alias)){
                return certificate;
            }
        }

        return null;
    }



}
