package com.example.bezbednost.bezbednost.repository;

import com.example.bezbednost.bezbednost.model.CustomCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICustomCertificateRepository extends JpaRepository<CustomCertificate, Integer> {
}
