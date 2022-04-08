package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.dto.certificate.CertificateDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetSingleCertificateDto;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;

public interface IPostCertificateService {
    void createCertificate(CertificateDto certificateDTO) throws OperatorCreationException,
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException, NoSuchProviderException, UnrecoverableKeyException;

    void exportCertificate(GetSingleCertificateDto certificateDto) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException;
}
