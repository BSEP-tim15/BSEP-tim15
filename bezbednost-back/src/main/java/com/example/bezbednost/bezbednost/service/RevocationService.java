package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IRevocationService;
import com.example.bezbednost.bezbednost.model.RevocationStatus;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Service
public class RevocationService implements IRevocationService {

    private X509Certificate issuerCert;

    private X509Certificate getIssuerCert(String pwd) {
            try {
                KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
                keyStore.load(new FileInputStream("rootCertificates.jsk"), pwd.toCharArray());
                issuerCert = (X509Certificate)keyStore.getCertificate("1817670488248");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        return issuerCert;
    }

    @Override
    // 0 - valid    1 - unknown    -1 - revoked
    public Integer checkCertificateStatus(String certificate, String pwd, int retry) {
        int result = -2;
        while (retry > 0) {
            try {

                byte[] p12Byte = new BASE64Decoder().decodeBuffer(certificate);
                KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
                //keyStore.load(new ByteInputStream(p12Byte, p12Byte.length), pwd.toCharArray());
                keyStore.load(new FileInputStream(certificate), pwd.toCharArray());
                Enumeration<String> aliases = keyStore.aliases();
                String alias = "";
                while (aliases.hasMoreElements()) {
                    alias = aliases.nextElement();
                    break;
                }
                //System.out.println(alias);
                X509Certificate cert = (X509Certificate)keyStore.getCertificate("1600674569228");
                OCSPReq ocspRequest = generateOcspRequest(cert, getIssuerCert(pwd));
                OCSPResp ocspResponse = generateOcspResponse(getOcspUrl(cert), ocspRequest);

                if (OCSPResp.SUCCESSFUL == ocspResponse.getStatus()) {
                    BasicOCSPResp basicOcspResponse = (BasicOCSPResp) ocspResponse.getResponseObject();
                    SingleResp[] responses = basicOcspResponse.getResponses();
                    if (responses != null && responses.length == 1) {
                        SingleResp response = responses[1];
                        CertificateStatus certificateStatus = response.getCertStatus();
                        if (certificateStatus == CertificateStatus.GOOD) {
                            result = 0;
                        } else {
                            if (certificateStatus instanceof RevokedStatus) {
                                result = -1;
                            } else if (certificateStatus instanceof UnknownStatus) {
                                result = 1;
                            }
                        }
                        retry = 0;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                retry--;
            }
        }
        return result;
    }

    // create OCSP request
    private OCSPReq generateOcspRequest(X509Certificate nextCertificate, X509Certificate nextIssuer) throws OperatorCreationException, CertificateEncodingException, IOException, OCSPException {

        //System.out.println(nextCertificate.getSerialNumber());
        //System.out.println(nextIssuer.getSerialNumber());

        OCSPReqBuilder requestBuilder = new OCSPReqBuilder();
        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();

        CertificateID certificateID = new CertificateID(new BcDigestCalculatorProvider().get(CertificateID.HASH_SHA1),
                new X509CertificateHolder(nextIssuer.getEncoded()), nextCertificate.getSerialNumber());

        requestBuilder.addRequest(certificateID);
        BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
        Extension extension = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(nonce.toByteArray()));
        requestBuilder.setRequestExtensions(new Extensions(new Extension[]{extension}));

        return requestBuilder.build();
    }

    // create OCSP response
    private OCSPResp generateOcspResponse(String url, OCSPReq oscpRequest) throws IOException {
        byte[] ocspRequestData = oscpRequest.getEncoded();
        System.out.println(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        try {
            connection.setRequestProperty("Content-Type", "application/ocsp-request");
            connection.setRequestProperty("Accept", "application/ocsp-response");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            OutputStream out = connection.getOutputStream();
            try {
                IOUtils.write(ocspRequestData, out);
                out.flush();
            } finally {
                IOUtils.closeQuietly(out);
            }

            byte[] responseBytes = IOUtils.toByteArray(connection.getInputStream());
            OCSPResp ocspResponse = new OCSPResp(responseBytes);

            return ocspResponse;

        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private String getOcspUrl(X509Certificate certificate) throws IOException{
        ASN1Primitive obj;
        try {
            System.out.println(Extension.authorityInfoAccess.getId()); // nije nyll
            System.out.println(Extension.subjectAlternativeName.getId());
            obj = getExtensionValue(certificate, Extension.authorityInfoAccess.getId());
        } catch (IOException ex) {
            return null;
        }

        if (obj == null) return null;

        AuthorityInformationAccess authorityInformationAccess = AuthorityInformationAccess.getInstance(obj);
        AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();

        for (AccessDescription accessDescription : accessDescriptions) {
            boolean correctAccessMethod = accessDescription.getAccessMethod().equals(X509ObjectIdentifiers.ocspAccessMethod);
            if (!correctAccessMethod) continue;

            GeneralName name = accessDescription.getAccessLocation();
            if (name.getTagNo() != GeneralName.uniformResourceIdentifier) continue;

            DERIA5String deria5String = DERIA5String.getInstance((ASN1TaggedObject) name.toASN1Primitive());

            return deria5String.getString();
        }

        return null;
    }

    private ASN1Primitive getExtensionValue(X509Certificate certificate, String id) throws IOException {
        System.out.println(id);
        byte[] bytes = certificate.getExtensionValue(id);
        if (bytes == null) return null;

        ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(bytes));
        ASN1OctetString octetString = (ASN1OctetString) in.readObject();
        in = new ASN1InputStream(new ByteArrayInputStream(octetString.getOctets()));

        return in.readObject();
    }

    @Override
    public RevocationStatus checkRevocationStatus(X509Certificate peerCert, X509Certificate issuerCert) {
        return null;
    }
}
