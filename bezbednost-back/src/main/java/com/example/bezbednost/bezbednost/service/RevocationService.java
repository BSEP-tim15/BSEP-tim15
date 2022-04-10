package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.PasswordsDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetSingleCertificateDto;
import com.example.bezbednost.bezbednost.iservice.IKeyService;
import com.example.bezbednost.bezbednost.iservice.IRevocationService;
import com.example.bezbednost.bezbednost.model.CustomCertificate;
import com.example.bezbednost.bezbednost.repository.ICustomCertificateRepository;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.cert.ocsp.jcajce.JcaBasicOCSPRespBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.stereotype.Service;
//import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.Date;
import java.util.List;

@Service
public class RevocationService implements IRevocationService {

    private final IKeyService keyService;
    private final ICustomCertificateRepository customCertificateRepository;

    public RevocationService(IKeyService keyService, ICustomCertificateRepository customCertificateRepository) {
        this.keyService = keyService;
        this.customCertificateRepository = customCertificateRepository;
    }

    @Override
    public boolean checkIfCertificateIsValid(GetSingleCertificateDto certificateDto) throws UnrecoverableKeyException, OCSPException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException {
        CustomCertificate certificate = customCertificateRepository.getBySerialNumber(certificateDto.getSerialNumber());
        if (checkIfCertificateIsRevoked(certificateDto)) return false;

        if (certificate.getIssuerSerialNumber().longValue() == 0)
            return isRootCertificateValid(certificateDto);
        else
            return isCertificateValid(certificate, certificateDto);
    }

    @Override
    public boolean checkIfCertificateIsRevoked(GetSingleCertificateDto certificateDto) throws
            OperatorCreationException, CertificateException, IOException, OCSPException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException {
        X509Certificate certificate = getCertificateBySerialNumber(certificateDto);

        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();

        CertificateID id = new CertificateID(digestCalculatorProvider.get(CertificateID.HASH_SHA1), new X509CertificateHolder(certificate.getEncoded()), certificate.getSerialNumber());
        OCSPReqBuilder reqBuilder = new OCSPReqBuilder();
        reqBuilder.addRequest(id);

        OCSPReq request = reqBuilder.build();
        BasicOCSPResp response = getOCSPResponse(request, certificateDto);

        System.out.println(response.getResponses()[0].getCertStatus() != null ? "Revoked" : "Good");

        return response.getResponses()[0].getCertStatus() != CertificateStatus.GOOD;
    }

    @Override
    public void revokeCertificate(BigInteger serialNumber) {
        CustomCertificate certificate = customCertificateRepository.getBySerialNumber(serialNumber);
        certificate.setRevoked(true);
        revokeSignedCertificates(serialNumber);
        customCertificateRepository.save(certificate);
    }

    private boolean isRootCertificateValid(GetSingleCertificateDto certificateDto) {
        X509Certificate cert = getCertificateBySerialNumber(certificateDto);
        try {
            cert.checkValidity(new Date());
            return true;
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isCertificateValid(CustomCertificate certificate, GetSingleCertificateDto certificateDto) throws UnrecoverableKeyException, OCSPException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException {
        do {
            CustomCertificate issuer = customCertificateRepository.getBySerialNumber(certificate.getIssuerSerialNumber());
            GetSingleCertificateDto issuerCertificate = new GetSingleCertificateDto(
                    issuer.getSerialNumber(), certificateDto.getRootPassword(), certificateDto.getIntermediatePassword(), certificateDto.getEndEntityPassword()
            );
            if (checkIfCertificateIsRevoked(issuerCertificate)) return false;
            if (!validateDatesAndKeys(certificateDto, issuerCertificate)) return false;

            certificate = issuer;
        } while(certificate.getIssuerSerialNumber().longValue() != 0);

        return true;
    }

    private boolean validateDatesAndKeys(GetSingleCertificateDto certificateDto, GetSingleCertificateDto certificateIssuerDto) {
        X509Certificate cert = getCertificateBySerialNumber(certificateDto);
        X509Certificate issuerCertificate = getCertificateBySerialNumber(certificateIssuerDto);

        try {
            cert.checkValidity(new Date());
            issuerCertificate.checkValidity(new Date());
        } catch (CertificateNotYetValidException | CertificateExpiredException e) {
            return false;
        }

        /*try {
            cert.verify(issuerCertificate.getPublicKey());
            return true;
        } catch (CertificateException | NoSuchAlgorithmException | SignatureException | InvalidKeyException | NoSuchProviderException e) {
            return false;
        }*/
        return true;
    }

    private void revokeSignedCertificates(BigInteger serialNumber) {
        List<CustomCertificate> signedCertificates = customCertificateRepository.getIssuedCertificates(serialNumber);
        for (CustomCertificate certificate : signedCertificates) {
            certificate.setRevoked(true);
            customCertificateRepository.save(certificate);
            revokeSignedCertificates(certificate.getSerialNumber());
        }
    }

    private BasicOCSPResp getOCSPResponse(OCSPReq request, GetSingleCertificateDto certificateDto) throws OCSPException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
        BigInteger serialNumber = request.getRequestList()[0].getCertID().getSerialNumber();
        X509Certificate certificate = getCertificateBySerialNumber(certificateDto);

        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();
        BasicOCSPRespBuilder respBuilder = new JcaBasicOCSPRespBuilder(certificate.getPublicKey(), digestCalculatorProvider.get(RespID.HASH_SHA1));

        if (isCertificateRevoked(serialNumber)) {
            respBuilder.addResponse(request.getRequestList()[0].getCertID(), new RevokedStatus(new Date(), 0));
        } else {
            respBuilder.addResponse(request.getRequestList()[0].getCertID(), CertificateStatus.GOOD);
        }

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder.setProvider("BC");
        ContentSigner contentSigner;

        try {
            contentSigner = builder.build(keyService.readPrivateKey(
                    "intermediateCertificates.jsk", certificateDto.getIntermediatePassword(), serialNumber.toString(), certificateDto.getIntermediatePassword()));
        } catch (OperatorCreationException e) {
            try {
                contentSigner = builder.build(keyService.readPrivateKey(
                        "rootCertificates.jsk", certificateDto.getRootPassword(), serialNumber.toString(), certificateDto.getRootPassword()));
            } catch (OperatorCreationException ex) {
                contentSigner = builder.build(keyService.readPrivateKey(
                        "end-entityCertificates.jsk", certificateDto.getEndEntityPassword(), serialNumber.toString(), certificateDto.getEndEntityPassword()));
            }
        }

        X509CertificateHolder[] responseList = new X509CertificateHolder[1];
        responseList[0] = new X509CertificateHolder(certificate.getEncoded());
        BasicOCSPResp response = respBuilder.build(contentSigner, responseList, new Date());

        return response;
    }

    private X509Certificate getCertificateBySerialNumber(GetSingleCertificateDto certificateDto) {
        X509Certificate certificate = (X509Certificate) keyService.readCertificate(
                "rootCertificates.jsk", certificateDto.getSerialNumber().toString(), certificateDto.getRootPassword());
        if (certificate == null) {
            if (keyService.readCertificate("intermediateCertificates.jsk", certificateDto.getSerialNumber().toString(), certificateDto.getIntermediatePassword()) != null) {
                certificate = (X509Certificate) keyService.readCertificate("intermediateCertificates.jsk", certificateDto.getSerialNumber().toString(), certificateDto.getIntermediatePassword());
            } else {
                certificate = (X509Certificate) keyService.readCertificate("end-entityCertificates.jsk", certificateDto.getSerialNumber().toString(), certificateDto.getEndEntityPassword());
            }
            return certificate;
        } else {
            return certificate;
        }
    }

    private boolean isCertificateRevoked(BigInteger serialNumber) {
        return customCertificateRepository.getBySerialNumber(serialNumber).isRevoked();
    }
}
