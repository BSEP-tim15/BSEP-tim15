package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

@Service
public class KeyService implements IKeyService {
    private KeyStore keyStore;
    private final Logger logger = LoggerFactory.getLogger("logerror");

    public KeyService(){
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException e) {
            logger.error("location=KeyService timestamp=" + LocalDateTime.now() + " status=failure message=" + e.getMessage());
        } catch (NoSuchProviderException e) {
            logger.error("location=GetCertificateService timestamp=" + LocalDateTime.now() + " status=failure message=" + e.getMessage());
        }
    }

    @Override
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            logger.error("location=GetCertificateService timestamp=" + LocalDateTime.now() + " action=GENERATE_KEY_PAIR status=failure message=" + e.getMessage());
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
    public Certificate[] getChain(String alias, String fileName, String password) throws KeyStoreException, NoSuchProviderException,
            IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS", "SUN");
        ks.load(new FileInputStream(fileName), password.toCharArray());
        if (ks.getCertificateChain(alias) != null);
        {
            return ks.getCertificateChain(alias);
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
        InputStream inputStream = new FileInputStream(keyStoreFile);
        BufferedInputStream in = new BufferedInputStream(inputStream);

        try {
            keyStore.load(in, keyStorePass.toCharArray());
            if(keyStore.isKeyEntry(alias)) {
                return (PrivateKey) keyStore.getKey(alias, pass.toCharArray());
            }
        } catch (IOException e) {
            logger.error("location=GetCertificateService timestamp=" + LocalDateTime.now() + " action=READ_PRIVATE_KEY status=failure message=" + e.getMessage());
        } finally {
            in.close();
            inputStream.close();
        }

        return null;
    }

    @Override
    public Certificate readCertificate(String fileName, String alias, String password) throws IOException {
        InputStream inputStream = new FileInputStream(fileName);
        BufferedInputStream in = new BufferedInputStream(inputStream);

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");

            try {
                keyStore.load(in, password.toCharArray());
                if (keyStore.isKeyEntry(alias)) {
                    return keyStore.getCertificate(alias);
                }
            } catch(IOException e) {
                logger.error("location=GetCertificateService timestamp=" + LocalDateTime.now() + " action=READ_CERTIFICATE status=failure message=" + e.getMessage());
            } finally {
                in.close();
                inputStream.close();
            }

        } catch (KeyStoreException | CertificateException | NoSuchProviderException | IOException | NoSuchAlgorithmException e) {
            logger.error("location=GetCertificateService timestamp=" + LocalDateTime.now() + " action=READ_CERTIFICATE status=failure message=" + e.getMessage());
        } finally {
            in.close();
            inputStream.close();
        }
        return null;
    }
}
