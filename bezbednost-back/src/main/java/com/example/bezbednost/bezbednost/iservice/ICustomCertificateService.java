package com.example.bezbednost.bezbednost.iservice;


import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface ICustomCertificateService {

    void createCustomCertificate(X509Certificate certificate) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException;
}
