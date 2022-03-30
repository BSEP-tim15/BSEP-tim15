package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.dto.CertificateDTO;
import org.bouncycastle.operator.OperatorCreationException;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface ICertificationService {
    X509Certificate createCertificate(CertificateDTO certificateDTO) throws OperatorCreationException, CertificateException;
}
