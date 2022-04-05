package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.dto.CertificateDto;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;

public interface IPostCertificateService {
    void createCertificate(CertificateDto certificateDTO) throws OperatorCreationException,
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException, NoSuchProviderException, UnrecoverableKeyException;

    void exportCertificate(BigInteger serialNumber) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException;
}
