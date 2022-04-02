package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IKeyService;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class KeyService implements IKeyService {
    private KeyStore keyStore;

    public KeyService(){
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void loadKeyStore(String fileName, char[] password) throws
            IOException, CertificateException, NoSuchAlgorithmException {
        if(fileName != null) {
            keyStore.load(new FileInputStream(fileName), password);
        } else {
            keyStore.load(null, password);
        }
    }

    @Override
    public void saveKeyStore(String fileName, char[] password) throws IOException, CertificateException,
            KeyStoreException, NoSuchAlgorithmException {
        keyStore.store(new FileOutputStream(fileName), password);
    }

    @Override
    public void writeToKeyStore(String alias, PrivateKey privateKey, char[] password, X509Certificate certificate)
            throws KeyStoreException {
        keyStore.setKeyEntry(alias, privateKey, password, new X509Certificate[] {certificate});
    }


}
