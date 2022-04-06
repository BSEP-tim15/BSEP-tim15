package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IKeyService;
import com.example.bezbednost.bezbednost.iservice.IRevocationService;
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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class RevocationService implements IRevocationService {

    private final IKeyService keyService;
    private final ICustomCertificateRepository customCertificateRepository;

    public RevocationService(IKeyService keyService, ICustomCertificateRepository customCertificateRepository) {
        this.keyService = keyService;
        this.customCertificateRepository = customCertificateRepository;
    }

    public boolean checkIfCertificateIsValid(BigInteger serialNumber) {
        return false;
    }

    public boolean checkIfCertificateIsRevoked(BigInteger serialNumber) {
        return false;
    }

    public void revokeCertificate(BigInteger serialNumber) {

    }

    private boolean isRootCertificateValid(/* prima Certificate certificate */) {
        return false;
    }

    private boolean isCertificateValid(/* prima Certificate certificate */) {
        return false;
    }

    private void revokeSignedCertificates(BigInteger serialNumber) {

    }

    private BasicOCSPResp getOCSPResponse(OCSPReq request) throws OperatorCreationException, OCSPException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
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

        ContentSigner contentSigner = builder.build(keyService.readPrivateKey("intermediateCertificates.jsk", "sifra", serialNumber.toString(), "sifra"));

        X509CertificateHolder[] responseList = new X509CertificateHolder[1];
        responseList[0] = new X509CertificateHolder(certificate.getEncoded());
        BasicOCSPResp response = respBuilder.build(contentSigner, responseList, new Date());

        return response;
    }

    private X509Certificate getCertificateBySerialNumber(BigInteger serialNumber) {
        return (X509Certificate) keyService.readCertificate("intermediateCertificates.jsk", "sifra", serialNumber.toString());
    }

    private boolean isCertificateRevoked(BigInteger serialNumber) {
        return customCertificateRepository.getBySerialNumber(serialNumber).isRevoked();
    }
}
