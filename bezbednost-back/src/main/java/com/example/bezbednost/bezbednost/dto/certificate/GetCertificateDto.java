package com.example.bezbednost.bezbednost.dto.certificate;

import lombok.Getter;
import lombok.Setter;

public class GetCertificateDto {

    @Getter @Setter private String certificateType;

    @Getter @Setter private String rootPassword;

    @Getter @Setter private String intermediatePassword;

    @Getter @Setter private String endEntityPassword;


    public GetCertificateDto() { }

    public GetCertificateDto(String certificateType, String rootPassword, String intermediatePassword, String endEntityPassword) {
        this.certificateType = certificateType;
        this.rootPassword = rootPassword;
        this.intermediatePassword = intermediatePassword;
        this.endEntityPassword = endEntityPassword;
    }
}
