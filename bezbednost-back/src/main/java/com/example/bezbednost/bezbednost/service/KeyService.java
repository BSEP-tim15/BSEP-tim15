package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IKeyService;
import org.springframework.stereotype.Service;

import java.security.*;

@Service
public class KeyService implements IKeyService {
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
}
