package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.ICustomCertificateService;
import com.example.bezbednost.bezbednost.iservice.IGetCertificateService;
import com.example.bezbednost.bezbednost.model.CustomCertificate;
import com.example.bezbednost.bezbednost.repository.ICustomCertificateRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class CustomCertificateService implements ICustomCertificateService {

    private final ICustomCertificateRepository customCertificateRepository;
    private final IGetCertificateService getCertificateService;

    public CustomCertificateService(ICustomCertificateRepository customCertificateRepository, IGetCertificateService getCertificateService) {
        this.customCertificateRepository = customCertificateRepository;
        this.getCertificateService = getCertificateService;
    }

    @Override
    public void createCustomCertificate(X509Certificate certificate) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        CustomCertificate customCertificate = new CustomCertificate();
        customCertificate.setRevoked(false);
        customCertificate.setId(9);
        customCertificate.setSerialNumber(certificate.getSerialNumber());
        customCertificate.setIssuerSerialNumber(getCertificateService.getSerialNumberOfParentCertificate(certificate.getSerialNumber()));
        customCertificateRepository.save(customCertificate);
    }
}
