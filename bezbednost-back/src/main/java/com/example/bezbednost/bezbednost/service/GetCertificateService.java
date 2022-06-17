package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.FileDto;
import com.example.bezbednost.bezbednost.dto.PasswordsDto;
import com.example.bezbednost.bezbednost.dto.certificate.CertificateDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetCertificateBySomeoneDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetCertificateDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetSingleCertificateDto;
import com.example.bezbednost.bezbednost.iservice.IGetCertificateService;
import com.example.bezbednost.bezbednost.iservice.IRevocationService;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GetCertificateService implements IGetCertificateService {
    private final KeyService keyService = new KeyService();
    private final IRevocationService revocationService;

    private Logger loggerError = LoggerFactory.getLogger("logerror");

    public GetCertificateService(IRevocationService revocationService) {
        this.revocationService = revocationService;
    }

    @Override
    public List<CertificateDto> getCertificates(GetCertificateDto certificate) throws KeyStoreException,
            NoSuchProviderException, IOException, CertificateException, NoSuchAlgorithmException {
        FileDto file = getFileInfo(certificate);
        GetSingleCertificateDto certificateDto = new GetSingleCertificateDto(
                certificate.getRootPassword(), certificate.getIntermediatePassword(), certificate.getEndEntityPassword()
        );
        if(file.getFileName().equals("all")){
            PasswordsDto passwords = new PasswordsDto(
                    certificate.getRootPassword(), certificate.getIntermediatePassword(), certificate.getEndEntityPassword()
            );
            return getAllCertificates(passwords);
        }
        else {
            return getCertificatesFromFile(file, certificateDto);
        }
    }

    @Override
    public List<CertificateDto> getCertificatesForEndEntity(GetCertificateDto certificate, String username) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        List<CertificateDto> endEntityCertificates = new ArrayList<CertificateDto>();
        for (CertificateDto certificateDto : getCertificates(certificate)) {
            if (certificateDto.getSubject().equals("CN=" + username)) {
                endEntityCertificates.add(certificateDto);
            }
        }
        return endEntityCertificates;
    }

    @Override
    public List<CertificateDto> getCertificatesForIntermediate(GetCertificateDto certificate, String username) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        List<CertificateDto> intermediateCertificates = new ArrayList<CertificateDto>();
        for (CertificateDto certificateDto : getCertificates(certificate)) {
            if (certificateDto.getSubject().equals("CN=" + username) || certificateDto.getIssuer().equals("CN=" + username)) {
                intermediateCertificates.add(certificateDto);
            }
        }
        return intermediateCertificates;
    }

    @Override
    public boolean canUserCreateCertificate(GetCertificateDto certificate, String username) throws
            CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException,
            UnrecoverableKeyException, OCSPException, OperatorCreationException {
        for (CertificateDto certificateDto : getCertificates(certificate)) {
            if (certificateDto.getSubject().equals("CN=" + username) && (certificateDto.getCertificateType().equals("root") || certificateDto.getCertificateType().equals("intermediate"))) {
                GetSingleCertificateDto singleCertificate = new GetSingleCertificateDto(certificateDto.getSerialNumber(),
                        certificate.getRootPassword(), certificate.getIntermediatePassword(), certificate.getEndEntityPassword());
                if(revocationService.checkIfCertificateIsValid(singleCertificate)) return true;
            }
        }
        return false;
    }

    private List<CertificateDto> getAllCertificates(PasswordsDto passwords) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        String rootFile = "rootCertificates.jks";
        String intermediateFile = "intermediateCertificates.jks";
        String endEntityFile = "end-entityCertificates.jks";
        GetSingleCertificateDto certificateDto = new GetSingleCertificateDto(
                passwords.getRootPassword(), passwords.getIntermediatePassword(), passwords.getEndEntityPassword()
        );
        return Stream.of(getCertificatesFromFile(new FileDto(rootFile, passwords.getRootPassword()), certificateDto),
                        getCertificatesFromFile(new FileDto(intermediateFile, passwords.getIntermediatePassword()), certificateDto),
                        getCertificatesFromFile(new FileDto(endEntityFile, passwords.getEndEntityPassword()), certificateDto))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private FileDto getFileInfo(GetCertificateDto certificate){
        if(Objects.equals(certificate.getCertificateType(), "root")){
            return new FileDto("rootCertificates.jks", certificate.getRootPassword());
        }
        else if(Objects.equals(certificate.getCertificateType(), "intermediate")){
            return new FileDto("intermediateCertificates.jks", certificate.getIntermediatePassword());
        }
        else if(Objects.equals(certificate.getCertificateType(), "end-entity")){
            return new FileDto("end-entityCertificates.jks", certificate.getEndEntityPassword());
        }
        else {
            return new FileDto("all", "");
        }
    }

    private List<CertificateDto> getCertificatesFromFile(FileDto file, GetSingleCertificateDto getCertificateDto) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> certificates = new ArrayList<>();
        InputStream inputStream= new FileInputStream(file.getFileName());
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
            keyStore.load(inputStream, file.getPassword().toCharArray());
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                X509Certificate cert  = (X509Certificate) keyStore.getCertificate(alias);
                getCertificateDto.setSerialNumber(cert.getSerialNumber());
                CertificateDto certificateDTO = new CertificateDto(cert.getSerialNumber(), cert.getIssuerDN().toString(),
                        cert.getSubjectDN().toString(), cert.getNotBefore(), cert.getNotAfter(),
                        getExtension(cert, "issuerAlternativeName"), getExtension(cert, "subjectAlternativeName"),
                        revocationService.checkIfCertificateIsValid(getCertificateDto));

                certificateDTO.setCertificateType(getCertificateType(file.getFileName()));
                certificates.add(certificateDTO);
            }
        } catch (FileNotFoundException | OperatorCreationException | OCSPException | UnrecoverableKeyException e){
            loggerError.error("location=GetCertificateService timestamp=" + LocalDateTime.now().toString() + " status=failure message=" + e.getMessage());
        } finally {
            inputStream.close();
        }

        return certificates;
    }

    private String getExtension(X509Certificate certificate, String extensionType) throws IOException {
        byte[] v = checkExtensionType(certificate, extensionType);
        GeneralNames gn = null;
        try{
            gn = GeneralNames.getInstance(X509ExtensionUtil.fromExtensionValue(v));
        }
        catch(NullPointerException e){
            return "";
        }
        try{
            GeneralName[] names = gn.getNames();
            for (GeneralName name : names) {
                if (name.getTagNo() == GeneralName.otherName) {
                    ASN1Sequence seq = ASN1Sequence.getInstance(name.getName());
                    ASN1Integer value = (ASN1Integer) seq.getObjectAt(1);
                    byte[] extensionBytes = value.getValue().toByteArray();
                    String extension = new String(extensionBytes, StandardCharsets.UTF_8);
                    return extension;
                }
            }
        }
        catch(NumberFormatException e){
            return "";
        }
        return "";
    }

    private byte[] checkExtensionType(X509Certificate certificate, String extensionType){
        if(extensionType.equals("reasonCode")){
            return certificate.getExtensionValue(Extension.reasonCode.getId());
        }
        else if(extensionType.equals("issuerAlternativeName")){
            return certificate.getExtensionValue(Extension.issuerAlternativeName.getId());
        }
        else if(extensionType.equals("subjectAlternativeName")){
            return certificate.getExtensionValue(Extension.subjectAlternativeName.getId());
        }
        else if(extensionType.equals("keyUsage")){
            return certificate.getExtensionValue(Extension.keyUsage.getId());
        }
        return null;
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
    public List<String> getIssuers(PasswordsDto passwords) throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException, UnrecoverableKeyException, OCSPException, OperatorCreationException {
        List<String> issuers = new ArrayList<>();
        for(CertificateDto certificate : getAllCertificates(passwords)){
            String issuer = certificate.getIssuer().replace("CN=", "");
            GetSingleCertificateDto certificateDto = new GetSingleCertificateDto(
                    certificate.getSerialNumber(), passwords.getRootPassword(), passwords.getIntermediatePassword(), passwords.getEndEntityPassword()
            );
            if(!isIssuerAdded(issuers, issuer) && revocationService.checkIfCertificateIsValid(certificateDto))
                issuers.add(issuer);
        }
        issuers = getIntermediateCertificatesSubjects(passwords, issuers);

        return issuers;
    }

    private List<String> getIntermediateCertificatesSubjects(PasswordsDto passwords, List<String> issuers) throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException, UnrecoverableKeyException, OCSPException, OperatorCreationException {
        for(CertificateDto certificate : getAllCertificates(passwords)){
            String issuer = certificate.getSubject().replace("CN=", "");
            GetSingleCertificateDto certificateDto = new GetSingleCertificateDto(
                    certificate.getSerialNumber(), passwords.getRootPassword(), passwords.getIntermediatePassword(), passwords.getEndEntityPassword()
            );
            if(Objects.equals(certificate.getCertificateType(), "intermediate") && !isIssuerAdded(issuers, issuer) &&
                    revocationService.checkIfCertificateIsValid(certificateDto))
                issuers.add(issuer);
        }

        return issuers;
    }

    private boolean isIssuerAdded(List<String> issuers, String issuer){
        return issuers.contains(issuer);
    }

    @Override
    public List<CertificateDto> getCertificatesBySubject(GetCertificateBySomeoneDto certificateDto) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> subjectCertificates = new ArrayList<>();
        for(CertificateDto certificate :
                getAllCertificates(new PasswordsDto(certificateDto.getRootPassword(), certificateDto.getIntermediatePassword(), certificateDto.getEndEntityPassword()))){
            String subjectUsername = "CN=" + certificateDto.getSomeone();
            if(Objects.equals(subjectUsername, certificate.getSubject())){
                subjectCertificates.add(certificate);
            }
        }

        return subjectCertificates;
    }

    @Override
    public Date getMaxDateForCertificate(GetCertificateBySomeoneDto certificateDto) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        List<CertificateDto> issuerCertificates = getCertificatesBySubject(certificateDto);
        Date maxDate = issuerCertificates.get(0).getValidTo();
        for (CertificateDto certificate : issuerCertificates) {
            if(certificate.getValidTo().before(maxDate)){
                maxDate = certificate.getValidTo();
            }
        }

        return maxDate;
    }

    @Override
    public CertificateDto getCertificateBySerialNumber(GetSingleCertificateDto certificateDto) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        for(CertificateDto certificate :
                getAllCertificates(new PasswordsDto(certificateDto.getRootPassword(), certificateDto.getIntermediatePassword(), certificateDto.getEndEntityPassword()))){
            if(Objects.equals(certificate.getSerialNumber(), certificateDto.getSerialNumber())){
                return certificate;
            }
        }

        return null;
    }

    @Override
    public BigInteger getSerialNumberOfParentCertificate(GetSingleCertificateDto certificateDto) throws
            CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        Certificate[] certificates = getCertificateChain(certificateDto);
        X509Certificate parentCertificate = (X509Certificate)certificates[1];
        return parentCertificate.getSerialNumber();
    }

    private Certificate[] getCertificateChain(GetSingleCertificateDto certificateDto) throws
            CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        Certificate[] certificates = keyService.getChain(certificateDto.getSerialNumber().toString(), "rootCertificates.jks", certificateDto.getRootPassword());
        if(certificates == null){
            certificates = keyService.getChain(certificateDto.getSerialNumber().toString(), "intermediateCertificates.jks", certificateDto.getIntermediatePassword());
            if(certificates == null){
                certificates = keyService.getChain(certificateDto.getSerialNumber().toString(), "end-entityCertificates.jks", certificateDto.getEndEntityPassword());
            }
        }
        return certificates;
    }

    @Override
    public List<BigInteger> getSerialNumbers() throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException {
        List<BigInteger> serialNumbers = new ArrayList<>();
        PasswordsDto passwordsDto = new PasswordsDto("sifra","sifra","sifra");
        List<CertificateDto> certificates = getAllCertificates(passwordsDto);
        for(CertificateDto certificate: certificates) {
            serialNumbers.add(certificate.getSerialNumber());
        }
        return serialNumbers;
    }

}
