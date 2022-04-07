package com.example.bezbednost.bezbednost.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "certificates")
public class CustomCertificate {

    @Id
    @Column(name = "cer_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Integer id;

    @Column(name = "serial_number", nullable = false)
    @Getter @Setter private BigInteger serialNumber;

    @Column(name = "is_revoked")
    @Getter @Setter private boolean isRevoked;

    @Column(name = "issuer_ser_num")
    @Getter @Setter private BigInteger issuerSerialNumber;

    public CustomCertificate() {
    }

    public CustomCertificate(Integer id, BigInteger serialNumber, boolean isRevoked, BigInteger issuerSerialNumber) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.isRevoked = isRevoked;
        this.issuerSerialNumber = issuerSerialNumber;
    }

    public CustomCertificate(BigInteger serialNumber, boolean isRevoked, BigInteger issuerSerialNumber) {
        this.serialNumber = serialNumber;
        this.isRevoked = isRevoked;
        this.issuerSerialNumber = issuerSerialNumber;
    }
}
