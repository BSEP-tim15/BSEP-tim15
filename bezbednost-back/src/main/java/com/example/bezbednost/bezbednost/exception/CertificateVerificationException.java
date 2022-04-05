package com.example.bezbednost.bezbednost.exception;

public class CertificateVerificationException extends Exception{

    public CertificateVerificationException(String message) {
        super(message);
    }

    public CertificateVerificationException(Throwable throwable) {
        super(throwable);
    }

    public CertificateVerificationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
