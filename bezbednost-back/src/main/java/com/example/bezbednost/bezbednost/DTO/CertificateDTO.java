package com.example.bezbednost.bezbednost.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class CertificateDTO {

    @Getter @Setter private String certificateType;

    @Getter @Setter private String issuer;

    @Getter @Setter private String subjectName;

    @Getter @Setter private String subjectUsername;

    @Getter @Setter private String subjectEmail;

    @Getter @Setter private String subjectCountry;

    @Getter @Setter private Date validFrom;

    @Getter @Setter private Date validTo;

    @Getter @Setter private String purpose;

    public CertificateDTO() { }

    public CertificateDTO(String certificateType, String issuer, String subjectName, String subjectUsername,
                          String subjectEmail, String subjectCountry, Date validFrom, Date validTo, String purpose) {
        this.certificateType = certificateType;
        this.issuer = issuer;
        this.subjectName = subjectName;
        this.subjectUsername = subjectUsername;
        this.subjectEmail = subjectEmail;
        this.subjectCountry = subjectCountry;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.purpose = purpose;
    }
}
