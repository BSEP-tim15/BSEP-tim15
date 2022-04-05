package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.CertificateDto;
import com.example.bezbednost.bezbednost.iservice.IKeyService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Service
public class KeyService implements IKeyService {
    private KeyStore keyStore;
    private GetCertificateService certificateService;
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
    public Certificate[] getChain(String alias, String fileName) throws KeyStoreException, NoSuchProviderException,
            IOException, CertificateException, NoSuchAlgorithmException {
        String password = "sifra";
        KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
        keyStore.load(new FileInputStream(fileName), password.toCharArray());
        if(keyStore.getCertificateChain(alias) != null);
        {
            return keyStore.getCertificateChain(alias);
        }
    }

    @Override
    public void writeToKeyStore(String alias, PrivateKey privateKey, char[] password, X509Certificate[] certificates)
            throws KeyStoreException {

        keyStore.setKeyEntry(alias, privateKey, password, certificates);
    }

    @Override
    public PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass)
            throws KeyStoreException, NoSuchProviderException, IOException, CertificateException,
            NoSuchAlgorithmException, UnrecoverableKeyException {
        keyStore = KeyStore.getInstance("JKS", "SUN");
        //ucitavamo podatke
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
        keyStore.load(in, keyStorePass.toCharArray());

        if(keyStore.isKeyEntry(alias)) {
            PrivateKey pk = (PrivateKey) keyStore.getKey(alias, pass.toCharArray());
            return pk;
        }
        return null;
    }


}
