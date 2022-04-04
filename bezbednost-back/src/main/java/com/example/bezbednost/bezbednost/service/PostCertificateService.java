package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.CertificateDto;
import com.example.bezbednost.bezbednost.iservice.IGetCertificateService;
import com.example.bezbednost.bezbednost.iservice.IKeyToolService;
import com.example.bezbednost.bezbednost.iservice.IPostCertificateService;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


@Service
public class PostCertificateService implements IPostCertificateService {
    private final KeyService keyService = new KeyService();
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private final IGetCertificateService getCertificateService;
    private final IKeyToolService keyToolService;

    public PostCertificateService(IGetCertificateService getCertificateService, IKeyToolService keyToolService) {
        this.getCertificateService = getCertificateService;
        this.keyToolService = keyToolService;
    }

    @Override
    public void createCertificate(CertificateDto certificateDTO) throws OperatorCreationException,
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException, NoSuchProviderException {
        KeyPair keyPair = keyService.generateKeyPair();
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC");
        ContentSigner contentSigner = builder.build(keyPair.getPrivate());
        X509v3CertificateBuilder certificateBuilder = generateCertificateBuilder(keyPair.getPublic(), certificateDTO);
        certificateBuilder.addExtension(Extension.subjectAlternativeName, true, setExtension(certificateDTO.getSubjectAlternativeName()));
        certificateBuilder.addExtension(Extension.issuerAlternativeName, true, setExtension(certificateDTO.getIssuerAlternativeName()));
        X509CertificateHolder certHolder = certificateBuilder.build(contentSigner);
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider("BC");
        X509Certificate certificate = certConverter.getCertificate(certHolder);
        certificate.verify(keyPair.getPublic());
        saveCertificate(certificate, keyPair.getPrivate(), certificateDTO.getCertificateType());
    }

    private GeneralNames setExtension(String extension){
        ASN1ObjectIdentifier oid = new ASN1ObjectIdentifier("1.2.3.4.5.6.7.8.9"); // some arbitrary non-existent OID number
        DERSequence seq = new DERSequence(new ASN1Encodable[] { oid, new ASN1Integer(extension.getBytes(StandardCharsets.UTF_8)) });
        ArrayList<GeneralName> namesList = new ArrayList<>();
        namesList.add(new GeneralName(GeneralName.otherName, seq));
        return GeneralNames.getInstance(new DERSequence(namesList.toArray(new GeneralName[] {})));
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

    private void saveCertificate(X509Certificate certificate, PrivateKey privateKey, String type) throws CertificateException,
            IOException, NoSuchAlgorithmException, KeyStoreException {
        String fileName = type + "Certificates.jsk";
        char[] password = getFilePassword(fileName);
        try {
            keyService.loadKeyStore(fileName, password);
        }
        catch(FileNotFoundException e){
            keyService.loadKeyStore(null, password);
            keyService.saveKeyStore(fileName, password);
        }
        keyService.writeToKeyStore(String.valueOf(certificate.getSerialNumber()), privateKey,
                password, certificate);
        keyService.saveKeyStore(fileName, password);
    }

    private char[] getFilePassword(String file){
        System.out.println("Please enter file password (" + file + ") :");
        Scanner scanner = new Scanner(System.in);
        String password = scanner.nextLine();
        return password.toCharArray();
    }

    @Override
    public void exportCertificate(BigInteger serialNumber) throws CertificateException, IOException,
            NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        CertificateDto certificate = getCertificateService.getCertificateBySerialNumber(serialNumber);
        String keyStoreName = certificate.getCertificateType() + "Certificates.jsk";
        //String keyStorePassword = Arrays.toString(getFilePassword(keyStoreName));
        String keyStorePassword = "sifra";
        String certificateName = serialNumber.toString();
        String command = "-exportcert -alias " + serialNumber + " -storepass " + keyStorePassword + " -file " +
                certificateName + ".cer -keystore " + keyStoreName;
        keyToolService.executeCommand(command);
    }



}
