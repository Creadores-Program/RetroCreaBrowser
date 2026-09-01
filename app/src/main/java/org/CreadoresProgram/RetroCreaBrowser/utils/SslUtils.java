package org.CreadoresProgram.RetroCreaBrowser.utils;

import android.net.http.SslCertificate;
import android.os.Build;
import android.os.Bundle;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SslUtils {

    private static List<X509Certificate> javaSecurityCerts = null;

    public static X509Certificate getX509Certificate(SslCertificate sslCert) {
        if (sslCert == null) return null;

        try {
            Bundle bundle = SslCertificate.saveState(sslCert);
            if (bundle == null) return null;
            
            byte[] bytes = bundle.getByteArray("x509-certificate");
            if (bytes == null) return null;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<X509Certificate> getAllJavaTrustedCertificates() {
        List<X509Certificate> allCerts = new ArrayList<>();
        try {
            String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(defaultAlgorithm);
            tmf.init((KeyStore) null);

            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    X509Certificate[] issuers = ((X509TrustManager) tm).getAcceptedIssuers();
                    if (issuers != null) {
                        for (X509Certificate cert : issuers) {
                            allCerts.add(cert);
                        }
                    }
                }
            }

            for (java.security.Provider provider : java.security.Security.getProviders()) {
                try {
                    TrustManagerFactory customTmf = TrustManagerFactory.getInstance(defaultAlgorithm, provider);
                    customTmf.init((KeyStore) null);
                    for (TrustManager tm : customTmf.getTrustManagers()) {
                        if (tm instanceof X509TrustManager) {
                            X509Certificate[] issuers = ((X509TrustManager) tm).getAcceptedIssuers();
                            if (issuers != null) {
                                for (X509Certificate cert : issuers) {
                                    allCerts.add(cert);
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allCerts;
    }

    public static void updateCertsJava(){
        javaSecurityCerts = getAllJavaTrustedCertificates();
    }

    public static boolean isCertInGlobalJavaTrustStore(X509Certificate cert) {
        if (cert == null) return false;
        if(javaSecurityCerts == null){
            updateCertsJava();
        }
        for (X509Certificate trusted : javaSecurityCerts) {
            if (trusted.equals(cert) || java.util.Arrays.equals(trusted.getSignature(), cert.getSignature())) {
                return true;
            }
        }
        return false;
    }
}
