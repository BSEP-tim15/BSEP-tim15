package com.example.bezbednost.bezbednost.dto.certificate;

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

    @Getter @Setter private String keyUsage;

    @Getter @Setter private String issuerAlternativeName;

    @Getter @Setter private String subjectAlternativeName;

    @Getter @Setter private boolean valid;

    @Getter @Setter private String rootPassword;

    @Getter @Setter private String intermediatePassword;

    @Getter @Setter private String endEntityPassword;


    public CertificateDto() { }

    public CertificateDto(BigInteger serialNumber, String certificateType, String issuer, String subject, Date validFrom,
                          Date validTo, String keyUsage, String issuerAlternativeName,
                          String subjectAlternativeName, boolean valid, String rootPassword, String intermediatePassword,
                          String endEntityPassword) {
        this.serialNumber = serialNumber;
        this.certificateType = certificateType;
        this.issuer = issuer;
        this.subject = subject;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.keyUsage = keyUsage;
        this.issuerAlternativeName = issuerAlternativeName;
        this.subjectAlternativeName = subjectAlternativeName;
        this.valid = valid;
        this.rootPassword = rootPassword;
        this.intermediatePassword = intermediatePassword;
        this.endEntityPassword = endEntityPassword;
    }

    public CertificateDto(BigInteger serialNumber, String issuer, String subject, Date validFrom, Date validTo,
                          String issuerAlternativeName, String subjectAlternativeName) {
        this.serialNumber = serialNumber;
        this.issuer = issuer;
        this.subject = subject;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.issuerAlternativeName = issuerAlternativeName;
        this.subjectAlternativeName = subjectAlternativeName;
    }

    public CertificateDto(BigInteger serialNumber, String issuer, String subject, Date validFrom, Date validTo,
                          String issuerAlternativeName, String subjectAlternativeName, boolean valid) {
        this.serialNumber = serialNumber;
        this.issuer = issuer;
        this.subject = subject;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.issuerAlternativeName = issuerAlternativeName;
        this.subjectAlternativeName = subjectAlternativeName;
        this.valid = valid;
    }

}
