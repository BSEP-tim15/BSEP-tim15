package com.example.bezbednost.bezbednost.iservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface IKeyService {
    KeyPair generateKeyPair();

    void loadKeyStore(String fileName, char[] password) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException;

    void saveKeyStore(String fileName, char[] password) throws IOException, CertificateException, KeyStoreException,
            NoSuchAlgorithmException;

    void writeToKeyStore(String alias, PrivateKey privateKey, char[] password, X509Certificate certificate) throws KeyStoreException;
}
