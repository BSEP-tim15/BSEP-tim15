package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IRevocationService;
import com.example.bezbednost.bezbednost.model.RevocationStatus;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
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
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Service
public class RevocationService implements IRevocationService {

    private X509Certificate issuerCert;

    private X509Certificate getIssuerCert() {
        if (issuerCert == null) {
            String issuerCertStr = "MIIEIjCCAwqgAwIBAgIIAd68xDltoBAwDQYJKoZIhvcNAQEFBQAwYjELMAkGA1UE\n" +
                    "BhMCVVMxEzARBgNVBAoTCkFwcGxlIEluYy4xJjAkBgNVBAsTHUFwcGxlIENlcnRp\n" +
                    "ZmljYXRpb24gQXV0aG9yaXR5MRYwFAYDVQQDEw1BcHBsZSBSb290IENBMB4XDTEz\n" +
                    "MDIwNzIxNDg0N1oXDTIzMDIwNzIxNDg0N1owgZYxCzAJBgNVBAYTAlVTMRMwEQYD\n" +
                    "VQQKDApBcHBsZSBJbmMuMSwwKgYDVQQLDCNBcHBsZSBXb3JsZHdpZGUgRGV2ZWxv\n" +
                    "cGVyIFJlbGF0aW9uczFEMEIGA1UEAww7QXBwbGUgV29ybGR3aWRlIERldmVsb3Bl\n" +
                    "ciBSZWxhdGlvbnMgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwggEiMA0GCSqGSIb3\n" +
                    "DQEBAQUAA4IBDwAwggEKAoIBAQDKOFSmy1aqyCQ5SOmM7uxfuH8mkbw0U3rOfGOA\n" +
                    "YXdkXqUHI7Y5/lAtFVZYcC1+xG7BSoU+L/DehBqhV8mvexj/avoVEkkVCBmsqtsq\n" +
                    "Mu2WY2hSFT2Miuy/axiV4AOsAX2XBWfODoWVN2rtCbauZ81RZJ/GXNG8V25nNYB2\n" +
                    "NqSHgW44j9grFU57Jdhav06DwY3Sk9UacbVgnJ0zTlX5ElgMhrgWDcHld0WNUEi6\n" +
                    "Ky3klIXh6MSdxmilsKP8Z35wugJZS3dCkTm59c3hTO/AO0iMpuUhXf1qarunFjVg\n" +
                    "0uat80YpyejDi+l5wGphZxWy8P3laLxiX27Pmd3vG2P+kmWrAgMBAAGjgaYwgaMw\n" +
                    "HQYDVR0OBBYEFIgnFwmpthhgi+zruvZHWcVSVKO3MA8GA1UdEwEB/wQFMAMBAf8w\n" +
                    "HwYDVR0jBBgwFoAUK9BpR5R2Cf70a40uQKb3R01/CF4wLgYDVR0fBCcwJTAjoCGg\n" +
                    "H4YdaHR0cDovL2NybC5hcHBsZS5jb20vcm9vdC5jcmwwDgYDVR0PAQH/BAQDAgGG\n" +
                    "MBAGCiqGSIb3Y2QGAgEEAgUAMA0GCSqGSIb3DQEBBQUAA4IBAQBPz+9Zviz1smwv\n" +
                    "j+4ThzLoBTWobot9yWkMudkXvHcs1Gfi/ZptOllc34MBvbKuKmFysa/Nw0Uwj6OD\n" +
                    "Dc4dR7Txk4qjdJukw5hyhzs+r0ULklS5MruQGFNrCk4QttkdUGwhgAqJTleMa1s8\n" +
                    "Pab93vcNIx0LSiaHP7qRkkykGRIZbVf1eliHe2iK5IaMSuviSRSqpd1VAKmuu0sw\n" +
                    "ruGgsbwpgOYJd+W+NKIByn/c4grmO7i77LpilfMFY0GCzQ87HUyVpNur+cmV6U/k\n" +
                    "TecmmYHpvPm0KdIBembhLoz2IYrF+Hjhga6/05Cdqa3zr/04GpZnMBxRpVzscYqC\n" +
                    "tGwPDBUf";

            try {
                byte[] issuerByte = new BASE64Decoder().decodeBuffer(issuerCertStr);
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                issuerCert = (X509Certificate) certificateFactory.generateCertificate(new ByteInputStream(issuerByte, issuerByte.length));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
                //System.out.println(cert.getSerialNumber());
                OCSPReq ocspRequest = generateOcspRequest(cert, getIssuerCert());
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
