package com.example.bezbednost.bezbednost.service;

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
    public boolean checkIfCertificateIsValid(BigInteger serialNumber) throws UnrecoverableKeyException, OCSPException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException {
        CustomCertificate certificate = customCertificateRepository.getBySerialNumber(serialNumber);
        if (checkIfCertificateIsRevoked(serialNumber)) return false;

        if (certificate.getIssuerSerialNumber().longValue() == 0)
            return isRootCertificateValid(certificate);
        else
            return isCertificateValid(certificate);
    }

    @Override
    public boolean checkIfCertificateIsRevoked(BigInteger serialNumber) throws OperatorCreationException, CertificateException, IOException, OCSPException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException {
        X509Certificate certificate = getCertificateBySerialNumber(serialNumber);

        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();

        CertificateID id = new CertificateID(digestCalculatorProvider.get(CertificateID.HASH_SHA1), new X509CertificateHolder(certificate.getEncoded()), certificate.getSerialNumber());
        OCSPReqBuilder reqBuilder = new OCSPReqBuilder();
        reqBuilder.addRequest(id);

        OCSPReq request = reqBuilder.build();
        BasicOCSPResp response = getOCSPResponse(request);

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

    private boolean isRootCertificateValid(CustomCertificate certificate) {
        X509Certificate cert = getCertificateBySerialNumber(certificate.getSerialNumber());
        try {
            cert.checkValidity(new Date());
            return true;
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isCertificateValid(CustomCertificate certificate) throws UnrecoverableKeyException, OCSPException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException {
        do {
            CustomCertificate issuer = customCertificateRepository.getBySerialNumber(certificate.getIssuerSerialNumber());
            if (checkIfCertificateIsRevoked(issuer.getSerialNumber())) return false;

            certificate = issuer;
        } while(certificate.getIssuerSerialNumber().longValue() != 0);

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

    private BasicOCSPResp getOCSPResponse(OCSPReq request) throws OCSPException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
        BigInteger serialNumber = request.getRequestList()[0].getCertID().getSerialNumber();
        X509Certificate certificate = getCertificateBySerialNumber(serialNumber);

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
            contentSigner = builder.build(keyService.readPrivateKey("intermediateCertificates.jsk", "sifra", serialNumber.toString(), "sifra"));
        } catch (OperatorCreationException e) {
            contentSigner = builder.build(keyService.readPrivateKey("rootCertificates.jsk", "sifra", serialNumber.toString(), "sifra"));
        }


        X509CertificateHolder[] responseList = new X509CertificateHolder[1];
        responseList[0] = new X509CertificateHolder(certificate.getEncoded());
        BasicOCSPResp response = respBuilder.build(contentSigner, responseList, new Date());

        return response;
    }

    private X509Certificate getCertificateBySerialNumber(BigInteger serialNumber) {
        X509Certificate certificate = (X509Certificate) keyService.readCertificate("rootCertificates.jsk", serialNumber.toString());
        if (certificate == null) {
            if (keyService.readCertificate("intermediateCertificates.jsk", serialNumber.toString()) != null) {
                certificate = (X509Certificate) keyService.readCertificate("intermediateCertificates.jsk", serialNumber.toString());
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
