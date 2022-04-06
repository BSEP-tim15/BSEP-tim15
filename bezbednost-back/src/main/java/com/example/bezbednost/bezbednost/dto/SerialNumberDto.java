package com.example.bezbednost.bezbednost.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class SerialNumberDto {

    @Getter @Setter private BigInteger serialNumber;

    public SerialNumberDto() {
    }

    public SerialNumberDto(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }
}
