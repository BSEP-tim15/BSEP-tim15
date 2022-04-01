package com.example.bezbednost.bezbednost.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

public class CertificateDto {

    @Getter @Setter private BigInteger serialNumber;

    @Getter @Setter private String certificateType;

    @Getter @Setter private String issuer;

    @Getter @Setter private String subject;

    @Getter @Setter private Date validFrom;

    @Getter @Setter private Date validTo;

    @Getter @Setter private String purpose;


    public CertificateDto() { }

    public CertificateDto(String certificateType, String issuer, String subject, Date validFrom, Date validTo, String purpose) {
        this.certificateType = certificateType;
        this.issuer = issuer;
        this.subject = subject;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.purpose = purpose;
    }

    public CertificateDto(BigInteger serialNumber, String issuer, String subject, Date validFrom, Date validTo) {
        this.serialNumber = serialNumber;
        this.issuer = issuer;
        this.subject = subject;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
}
