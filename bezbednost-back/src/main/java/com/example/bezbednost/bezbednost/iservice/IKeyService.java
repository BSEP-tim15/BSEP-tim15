package com.example.bezbednost.bezbednost.iservice;

import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface IKeyService {
    KeyPair generateKeyPair();

    void loadKeyStore(String fileName, char[] password) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException;

    void saveKeyStore(String fileName, char[] password) throws IOException, CertificateException, KeyStoreException,
            NoSuchAlgorithmException;

    Certificate[] getChain(String alias, String fileName) throws KeyStoreException, NoSuchProviderException,
            IOException, CertificateException, NoSuchAlgorithmException;

    void writeToKeyStore(String alias, PrivateKey privateKey, char[] password, X509Certificate[] certificates) throws KeyStoreException;

    PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass) throws KeyStoreException, NoSuchProviderException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException;

    Certificate readCertificate(String fileName, String alias);
}
