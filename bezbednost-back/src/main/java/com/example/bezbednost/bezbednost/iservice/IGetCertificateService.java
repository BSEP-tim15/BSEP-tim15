package com.example.bezbednost.bezbednost.iservice;

import com.example.bezbednost.bezbednost.dto.PasswordsDto;
import com.example.bezbednost.bezbednost.dto.certificate.CertificateDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetCertificateBySomeoneDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetCertificateDto;
import com.example.bezbednost.bezbednost.dto.certificate.GetSingleCertificateDto;
import com.example.bezbednost.bezbednost.dto.CertificateDto;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.List;

public interface IGetCertificateService {

    List<CertificateDto> getCertificates(GetCertificateDto certificate) throws CertificateException,
            IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException;

    List<String> getIssuers(PasswordsDto passwords) throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException;

    List<CertificateDto> getCertificatesBySubject(GetCertificateBySomeoneDto certificateDto) throws CertificateException, IOException,
            NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException;

    Date getMaxDateForCertificate(GetCertificateBySomeoneDto certificateDto) throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException;

    CertificateDto getCertificateBySerialNumber(GetSingleCertificateDto certificateDto) throws CertificateException, IOException,
            NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException;

    BigInteger getSerialNumberOfParentCertificate(GetSingleCertificateDto certificateDto) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException;

}
