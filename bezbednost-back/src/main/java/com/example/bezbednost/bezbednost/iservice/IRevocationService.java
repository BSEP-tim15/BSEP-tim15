package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.dto.certificate.GetSingleCertificateDto;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public interface IRevocationService {

     boolean checkIfCertificateIsRevoked(GetSingleCertificateDto certificateDto) throws OperatorCreationException, CertificateException, IOException, OCSPException,
            UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException;

    void revokeCertificate(BigInteger serialNumber);

    boolean checkIfCertificateIsValid(GetSingleCertificateDto certificateDto) throws UnrecoverableKeyException, OCSPException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException;
}
