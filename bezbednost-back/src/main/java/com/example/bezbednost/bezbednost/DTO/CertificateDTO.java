package com.example.bezbednost.bezbednost.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class CertificateDTO {

    @Getter @Setter private String certificateType;

    @Getter @Setter private String issuer;

    @Getter @Setter private String subject;

    @Getter @Setter private Date validFrom;

    @Getter @Setter private Date validTo;

    @Getter @Setter private String purpose;

    public CertificateDTO() { }

    public CertificateDTO(String certificateType, String issuer, String subject, Date validFrom, Date validTo, String purpose) {
        this.certificateType = certificateType;
        this.issuer = issuer;
        this.subject = subject;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.purpose = purpose;
    }
}
