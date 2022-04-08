package com.example.bezbednost.bezbednost.dto.certificate;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class GetCertificateBySomeoneDto {

    @Getter @Setter private String someone;

    @Getter @Setter private String rootPassword;

    @Getter @Setter private String intermediatePassword;

    @Getter @Setter private String endEntityPassword;


    public GetCertificateBySomeoneDto() {  }

    public GetCertificateBySomeoneDto(String someone, String rootPassword, String intermediatePassword, String endEntityPassword) {
        this.someone = someone;
        this.rootPassword = rootPassword;
        this.intermediatePassword = intermediatePassword;
        this.endEntityPassword = endEntityPassword;
    }
}
