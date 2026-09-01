package org.CreadoresProgram.RetroCreaBrowser.utils;
import android.net.http.SslCertificate;
import android.os.Build;
import android.os.Bundle;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.util.Enumeration;

public class SslUtils{
    public static X509Certificate getX509Certificate(SslCertificate sslCert){
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
    public static boolean isCertJavaTrustStore(X509Certificate cert, KeyStore myTrustStore) {
        if (cert == null || myTrustStore == null) return false;

        try {
            Enumeration<String> aliases = myTrustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                X509Certificate trustedCert = (X509Certificate) myTrustStore.getCertificate(alias);
                
                if (trustedCert != null) {
                    if (trustedCert.equals(cert) || java.util.Arrays.equals(trustedCert.getSignature(), cert.getSignature())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
