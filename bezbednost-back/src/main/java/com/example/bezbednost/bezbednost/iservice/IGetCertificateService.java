package com.example.bezbednost.bezbednost.iservice;

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

    List<CertificateDto> getCertificates(String certificateType) throws CertificateException,
            IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException;

    List<String> getIssuers() throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException, UnrecoverableKeyException, OCSPException, OperatorCreationException;

    List<CertificateDto> getCertificatesBySubject(String subject) throws CertificateException, IOException,
            NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException;

    Date getMaxDateForCertificate(String issuer) throws CertificateException, IOException, NoSuchAlgorithmException,
            KeyStoreException, NoSuchProviderException;

    CertificateDto getCertificateBySerialNumber(BigInteger serialNumber) throws CertificateException, IOException,
            NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException;

    BigInteger getSerialNumberOfParentCertificate(BigInteger serialNumber) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException;

}
