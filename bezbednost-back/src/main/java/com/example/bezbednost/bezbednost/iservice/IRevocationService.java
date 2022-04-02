package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.exception.CertificateVerificationException;
import com.example.bezbednost.bezbednost.model.RevocationStatus;

import java.security.cert.X509Certificate;

public interface IRevocationService {
    Integer checkCertificateStatus(String certificate, String pwd, int retry);

    RevocationStatus checkRevocationStatus(X509Certificate peerCert, X509Certificate issuerCert) throws CertificateVerificationException;
}
