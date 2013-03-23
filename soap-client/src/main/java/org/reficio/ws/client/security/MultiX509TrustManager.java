package org.reficio.ws.client.security;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public class MultiX509TrustManager implements X509TrustManager {

    private final List<X509TrustManager> managers;

    public MultiX509TrustManager(List<X509TrustManager> managers) {
        this.managers = new ArrayList<X509TrustManager>(managers);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
        List<CertificateException> exceptions = new ArrayList<CertificateException>();
        try {
            for (X509TrustManager manager : managers) {
                manager.checkClientTrusted(x509Certificates, authType);
            }
        } catch (CertificateException ex) {
            exceptions.add(ex);
        }
        if (exceptions.size() >= managers.size()) {
            throw exceptions.iterator().next();
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
        List<CertificateException> exceptions = new ArrayList<CertificateException>();
        try {
            for (X509TrustManager manager : managers) {
                manager.checkServerTrusted(x509Certificates, authType);
            }
        } catch (CertificateException ex) {
            exceptions.add(ex);
        }
        if (exceptions.size() >= managers.size()) {
            throw exceptions.iterator().next();
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        List<X509Certificate> certs = new ArrayList<X509Certificate>();
        for (X509TrustManager manager : managers) {
            for (X509Certificate cert : manager.getAcceptedIssuers()) {
                certs.add(cert);
            }
        }
        return certs.toArray(new X509Certificate[]{});
    }
}