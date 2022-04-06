package com.example.bezbednost.bezbednost.repository;

import com.example.bezbednost.bezbednost.model.CustomCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface ICustomCertificateRepository extends JpaRepository<CustomCertificate, Integer> {

    @Query("SELECT c FROM CustomCertificate c WHERE c.serialNumber=?1")
    CustomCertificate getBySerialNumber(BigInteger serialNumber);

    @Query("SELECT c FROM CustomCertificate c WHERE c.issuerSerialNumber=?1")
    List<CustomCertificate> getIssuedCertificates(BigInteger serialNumber);

}
