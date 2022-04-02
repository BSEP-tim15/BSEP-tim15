package com.example.bezbednost.bezbednost.iservice;

public interface IRevocationService {
    Integer checkCertificateStatus(String certificate, String pwd, int retry);
}
