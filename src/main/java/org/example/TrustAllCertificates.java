package org.example;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustAllCertificates implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // Nu facem nimic pentru a verifica clientul
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // Nu facem nimic pentru a verifica serverul
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
