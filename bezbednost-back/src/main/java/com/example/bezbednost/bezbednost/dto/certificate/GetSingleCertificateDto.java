package com.example.bezbednost.bezbednost.dto.certificate;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class GetSingleCertificateDto {

    @Getter @Setter private BigInteger serialNumber;

    @Getter @Setter private String rootPassword;

    @Getter @Setter private String intermediatePassword;

    @Getter @Setter private String endEntityPassword;


    public GetSingleCertificateDto() { }

    public GetSingleCertificateDto(BigInteger serialNumber, String rootPassword, String intermediatePassword, String endEntityPassword) {
        this.serialNumber = serialNumber;
        this.rootPassword = rootPassword;
        this.intermediatePassword = intermediatePassword;
        this.endEntityPassword = endEntityPassword;
    }
}
