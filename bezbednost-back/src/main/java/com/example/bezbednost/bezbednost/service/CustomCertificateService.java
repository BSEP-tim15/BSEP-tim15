package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.ICustomCertificateService;
import com.example.bezbednost.bezbednost.model.CustomCertificate;
import com.example.bezbednost.bezbednost.repository.ICustomCertificateRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomCertificateService implements ICustomCertificateService {

    private final ICustomCertificateRepository customCertificateRepository;

    public CustomCertificateService(ICustomCertificateRepository customCertificateRepository) {
        this.customCertificateRepository = customCertificateRepository;
    }

    @Override
    public void save(CustomCertificate customCertificate) {
        customCertificateRepository.save(customCertificate);
    }
}
