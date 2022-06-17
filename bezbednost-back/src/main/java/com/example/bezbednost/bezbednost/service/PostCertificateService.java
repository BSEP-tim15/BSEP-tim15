package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.PasswordsDto;
import com.example.bezbednost.bezbednost.dto.certificate.CertificateDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetSingleCertificateDto;
import com.example.bezbednost.bezbednost.exception.InvalidInputException;
import com.example.bezbednost.bezbednost.iservice.*;
import com.example.bezbednost.bezbednost.validation.RegexValidator;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


@Service
public class PostCertificateService implements IPostCertificateService {
    private Random random = SecureRandom.getInstanceStrong();
    private final KeyService keyService = new KeyService();
    private String chainAlias = "";
    private final IGetCertificateService getCertificateService;
    private final IKeyToolService keyToolService;
    private final ICustomCertificateService customCertificateService;
    private final IValidationService validationService;

    public PostCertificateService(IGetCertificateService getCertificateService, IKeyToolService keyToolService,
                                  ICustomCertificateService customCertificateService, IValidationService validationService) throws NoSuchAlgorithmException {
        this.getCertificateService = getCertificateService;
        this.keyToolService = keyToolService;
        this.customCertificateService = customCertificateService;
        this.validationService = validationService;
    }

    @Override
    public void createCertificate(CertificateDto certificateDTO) throws OperatorCreationException,
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
            UnrecoverableKeyException, InvalidInputException {
        KeyPair keyPair = keyService.generateKeyPair();
        KeyPair subjectKey =
                getSubjectKey(certificateDTO.getIssuer(), new PasswordsDto(certificateDTO.getRootPassword(),
                        certificateDTO.getIntermediatePassword(), certificateDTO.getEndEntityPassword()));
        if(subjectKey == null){
            subjectKey = keyPair;
        }
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC");
        ContentSigner contentSigner = builder.build(subjectKey.getPrivate());
        X509v3CertificateBuilder certificateBuilder = generateCertificateBuilder(keyPair.getPublic(), certificateDTO);
        certificateBuilder.addExtension(Extension.subjectAlternativeName, true, setExtension(certificateDTO.getSubjectAlternativeName()));
        certificateBuilder.addExtension(Extension.issuerAlternativeName, true, setExtension(certificateDTO.getIssuerAlternativeName()));
        SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
        certificateBuilder.addExtension(Extension.subjectKeyIdentifier, true, subjectKeyIdentifier);
        certificateBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign));
        certificateBuilder.addExtension(Extension.authorityKeyIdentifier, true, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(subjectKey.getPublic()));

        if(certificateDTO.getCertificateType().equals("end-entity")){
            certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
        }
        else{
            certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        }
        X509CertificateHolder certHolder = certificateBuilder.build(contentSigner);
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider("BC");
        X509Certificate certificate = certConverter.getCertificate(certHolder);
        try
        {
            certificate.verify(subjectKey.getPublic());
        }
        catch(Exception e){
            throw new CertificateException("Certificate not trusted",e);
        }
        System.out.println("\nValidacija uspesna :)");

        if (validationService.isValid(RegexValidator.ONLY_LETTERS_REGEX, certificateDTO.getIssuer()) &&
                validationService.isValid(RegexValidator.ONLY_LETTERS_REGEX, certificateDTO.getSubject()) &&
                validationService.isValid(RegexValidator.ONLY_LETTERS_REGEX, certificateDTO.getIssuerAlternativeName()) &&
                validationService.isValid(RegexValidator.ONLY_LETTERS_REGEX, certificateDTO.getSubjectAlternativeName()) && !containsDangerousCharacters(certificateDTO)) {

            saveCertificate(certificate, keyPair.getPrivate(), certificateDTO.getCertificateType(),
                    new PasswordsDto(certificateDTO.getRootPassword(), certificateDTO.getIntermediatePassword(), certificateDTO.getEndEntityPassword()));
        } else {
            throw new InvalidInputException("Invalid input!");
        }

    }

    private KeyPair getSubjectKey(String issuer, PasswordsDto passwords) throws KeyStoreException, NoSuchProviderException, IOException,
            CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyPair key = null;
        try {
            key = findSubjectKey("rootCertificates.jks", issuer, passwords.getRootPassword());
        }
        catch(FileNotFoundException e){
            key = null;
        }
        if(key == null){
            try {
                key = findSubjectKey("intermediateCertificates.jks",issuer, passwords.getIntermediatePassword());
            }
            catch(FileNotFoundException e){
                key = null;
            }
        }
        if(key != null){
            return key;
        }
        else{
            return null;
        }
    }

    private KeyPair findSubjectKey(String fileName, String issuer, String password) throws KeyStoreException, NoSuchProviderException, IOException,
            CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
        keyStore.load(new FileInputStream(fileName), password.toCharArray());
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
            String newIssuer = "CN=" + issuer;
            if(cert.getSubjectDN().toString().equals(newIssuer)){
                chainAlias = alias;
                PrivateKey privateKey = keyService.readPrivateKey(fileName, password, alias, password);
                return new KeyPair(cert.getPublicKey(), privateKey);
            }
        }
        return null;
    }

    private GeneralNames setExtension(String extension){
        ASN1ObjectIdentifier oid = new ASN1ObjectIdentifier("1.2.3.4.5.6.7.8.9"); // some arbitrary non-existent OID number
        if(extension == null){
            extension = "";
        }
        DERSequence seq = new DERSequence(new ASN1Encodable[] { oid, new ASN1Integer(extension.getBytes(StandardCharsets.UTF_8)) });
        ArrayList<GeneralName> namesList = new ArrayList<>();
        namesList.add(new GeneralName(GeneralName.otherName, seq));
        return GeneralNames.getInstance(new DERSequence(namesList.toArray(new GeneralName[] {})));
    }

    private X509v3CertificateBuilder generateCertificateBuilder(PublicKey publicKey, CertificateDto certificateDTO)
            throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        X500NameBuilder issuer = new X500NameBuilder(BCStyle.INSTANCE);
        issuer.addRDN(BCStyle.CN, certificateDTO.getIssuer());
        X500NameBuilder subject = new X500NameBuilder(BCStyle.INSTANCE);
        subject.addRDN(BCStyle.CN, certificateDTO.getSubject());
        BigInteger serialNumber = createSerialNumber();
        while(getCertificateService.getSerialNumbers().contains(serialNumber)){
            serialNumber = createSerialNumber();
        }
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
        int len = maxLimit.bitLength();
        BigInteger res = new BigInteger(len, this.random);
        if (res.compareTo(minLimit) < 0)
            res = res.add(minLimit);
        if (res.compareTo(bigInteger) >= 0)
            res = res.mod(bigInteger).add(minLimit);
        return res;
    }

    private void saveCertificate(X509Certificate certificate, PrivateKey privateKey, String type, PasswordsDto passwords) throws CertificateException,
            IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        String fileName = type + "Certificates.jks";
        String password = getFilePassword(fileName, passwords);
        try {
            keyService.loadKeyStore(fileName, password.toCharArray());
        }
        catch(FileNotFoundException e){
            keyService.loadKeyStore(null, password.toCharArray());
            keyService.saveKeyStore(fileName, password.toCharArray());
        }
        X509Certificate[] certificates = null;
        if(chainAlias.isEmpty()){
            certificates = new X509Certificate[1];
            certificates[0] = certificate;
        }
        else{
            Certificate[] chain = null;
            if(keyService.getChain(chainAlias, "rootCertificates.jks", passwords.getRootPassword()) != null){
                chain = keyService.getChain(chainAlias, "rootCertificates.jks", passwords.getRootPassword());
            }
            else if(keyService.getChain(chainAlias, "intermediateCertificates.jks", passwords.getIntermediatePassword()) != null){
                chain = keyService.getChain(chainAlias, "intermediateCertificates.jks", passwords.getIntermediatePassword());
            }
            certificates = new X509Certificate[Objects.requireNonNull(chain).length + 1];
            certificates[0] = certificate;
            int i = 1;
            for(Certificate cer: chain){
                certificates[i] = (X509Certificate) cer;
                i++;
            }
        }

        keyService.writeToKeyStore(String.valueOf(certificate.getSerialNumber()), privateKey,
                password.toCharArray(), certificates);
        keyService.saveKeyStore(fileName, password.toCharArray());
        customCertificateService.createCustomCertificate(certificate, type, password);
    }

    private String getFilePassword(String fileName, PasswordsDto passwords){
        if(fileName.contains("root")){
            return passwords.getRootPassword();
        }
        else if(fileName.contains("intermediate")){
            return passwords.getIntermediatePassword();
        }
        else {
            return passwords.getEndEntityPassword();
        }
    }

    @Override
    public void exportCertificate(GetSingleCertificateDto certificateDto) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        CertificateDto certificate = getCertificateService.getCertificateBySerialNumber(certificateDto);
        String keyStoreName = certificate.getCertificateType() + "Certificates.jks";
        String keyStorePassword = getFilePassword(keyStoreName,
                new PasswordsDto(certificateDto.getRootPassword(), certificateDto.getIntermediatePassword(), certificateDto.getEndEntityPassword()));
        String certificateName = certificateDto.getSerialNumber().toString();
        String command = "-exportcert -alias " + certificateDto.getSerialNumber() + " -storepass " + keyStorePassword +
                " -file " + certificateName + ".cer -keystore " + keyStoreName;
        keyToolService.executeCommand(command);
    }

    private boolean containsDangerousCharacters(CertificateDto certificateDto) {
        return validationService.containsDangerousCharacters(certificateDto.getIssuer()) || validationService.containsDangerousCharacters(certificateDto.getSubject()) ||
                validationService.containsDangerousCharacters(certificateDto.getSubjectAlternativeName()) || validationService.containsDangerousCharacters(certificateDto.getIssuerAlternativeName());
    }

}
