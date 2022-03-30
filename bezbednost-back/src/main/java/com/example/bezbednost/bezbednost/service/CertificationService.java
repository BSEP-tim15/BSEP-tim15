package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.CertificateDTO;
import com.example.bezbednost.bezbednost.iservice.ICertificationService;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;

import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


@Service
public class CertificationService implements ICertificationService {
    private KeyService keyService = new KeyService();

    @Override
    public X509Certificate createCertificate(CertificateDTO certificateDTO) throws OperatorCreationException,
            CertificateException {
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC");
        KeyPair keyPair = keyService.generateKeyPair();
        ContentSigner contentSigner = builder.build(keyPair.getPrivate());
        X509v3CertificateBuilder certificateBuilder = generateCertificateBuilder(keyPair.getPublic(), certificateDTO);
        X509CertificateHolder certHolder = certificateBuilder.build(contentSigner);
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider("BC");
        return certConverter.getCertificate(certHolder);
    }

    private X509v3CertificateBuilder generateCertificateBuilder(PublicKey publicKey, CertificateDTO certificateDTO){
        X500NameBuilder issuer = new X500NameBuilder(BCStyle.INSTANCE);
        issuer.addRDN(BCStyle.CN, certificateDTO.getIssuer());
        X500NameBuilder subject = new X500NameBuilder(BCStyle.INSTANCE);
        subject.addRDN(BCStyle.CN, certificateDTO.getSubject());
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuer.build(),
                createSerialNumber(),
                certificateDTO.getValidFrom(),
                certificateDTO.getValidTo(),
                subject.build(),
                publicKey);
        return certGen;
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

}
