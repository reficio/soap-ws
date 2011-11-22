package com.centeractive.ws.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Encoder;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static com.centeractive.ws.client.HttpConstants.*;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 11/11/11
 * Time: 12:55 PM
 */
public final class SoapClient {

    private final static Log log = LogFactory.getLog(SoapClient.class);

    private URL serverUrl;
    private String basicAuthEncoded;
    private boolean tlsEnabled;
    private KeyStore keyStore;
    private HttpURLConnection connection;

    private boolean strictHostVerification = false;
    private String sslContext = "SSLv3";
    private SSLContext context;
    private Proxy proxy;
    private SSLSocketFactory sslSocketFactory;
    private String proxyAuthEncoded;

    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private SoapClient() {
    }

    class SoapHostnameVerifier implements com.sun.net.ssl.HostnameVerifier, javax.net.ssl.HostnameVerifier {
        public boolean verify(String urlHostName, String certHostName) {
            return true;
        }

        public boolean verify(String urlHost, SSLSession sslSession) {
            return true;
        }
    }

    private void configureTls() {
        if (tlsEnabled == false)
            return;
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            context = SSLContext.getInstance(sslContext);
            context.init(null, new TrustManager[]{defaultTrustManager}, null);
            sslSocketFactory = context.getSocketFactory();
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            if (strictHostVerification == false) {
                ((HttpsURLConnection) connection).setHostnameVerifier(new SoapHostnameVerifier());
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private void openConnection() throws IOException {
        if (proxy != null) {
            connection = (HttpURLConnection) serverUrl.openConnection(proxy);
        } else {
            connection = (HttpURLConnection) serverUrl.openConnection();
        }
    }

    private void configureConnection() throws ProtocolException {
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(POST);
        if (basicAuthEncoded != null) {
            connection.setRequestProperty("Authorization", "Basic " + basicAuthEncoded);
        }
        if (proxyAuthEncoded != null) {
            connection.setRequestProperty("Proxy-Authorization", "Basic " + basicAuthEncoded);
        }
    }

    private void decorateConnectionWithSoap(String soapAction, String requestEnvelope) throws ProtocolException {
        if (soapAction != null) {
            connection.setRequestProperty("SOAPAction", soapAction);
        }
        if (requestEnvelope.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
            connection.setRequestProperty("Content-Type", MIMETYPE_TEXT_XML);
        } else if (requestEnvelope.contains("http://www.w3.org/2003/05/soap-envelope")) {
            connection.setRequestProperty("Content-Type", "application/soap+xml");
        }
        connection.setRequestProperty("Content-Length", Integer.toString(requestEnvelope.length()));
    }

    private String transmit(String data) {
        try {
            return performTransmission(data);
        } catch (IOException ex) {
            properlyHandleTransmissionError(ex);
        } finally {
            cleanupResources();
        }
        return null;
    }

    private String performTransmission(String data) throws IOException {
        Writer outputWriter = null;
        try {
            outputStream = connection.getOutputStream();
            outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(data);
            outputWriter.flush();

            inputStream = connection.getInputStream();
            StringBuilder response = new StringBuilder();
            int inputChar;
            while ((inputChar = inputStream.read()) != -1) {
                response.append((char) inputChar);
            }
            return response.toString();
        } finally {
            if (outputWriter != null)
                outputWriter.close();
        }
    }

    private void properlyHandleTransmissionError(IOException ex) {
        try {
            int respCode = connection.getResponseCode();
            log.info(respCode);
            InputStream errorStream = ((HttpURLConnection) connection).getErrorStream();
            int ret = 0;
            while ((ret = errorStream.read()) > 0) {
            }
            errorStream.close();
        } catch (IOException e) {
            // ignore
        } finally {
            throw new RuntimeException(ex);
        }
    }

    private void cleanupResources() {
        try {
            if (inputStream != null)
                inputStream.close();
        } catch (IOException e) {
            // ignore
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public SoapClient disconnect() {
        connection.disconnect();
        return this;
    }

    public String post(String requestEnvelope) {
        return post(null, requestEnvelope);
    }


    public String post(String soapAction, String requestEnvelope) {
        log.info(String.format("Sending request to host=[%s] action=[%s] request:\n%s", serverUrl.toString(),
                soapAction, requestEnvelope));
        try {
            openConnection();
            configureTls();
            configureConnection();
            decorateConnectionWithSoap(soapAction, requestEnvelope);
            String response = transmit(requestEnvelope);
            log.info("Received response:\n" + requestEnvelope);
            return response;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class Builder {
        SoapClient client = new SoapClient();

        private String keyStorePath;
        private String keyStoreType = "JKS";
        private String keyStorePassword;

        private Proxy.Type proxyType = Proxy.Type.DIRECT;
        private String proxyHost;
        private int proxyPort;

        private String encodeBasicCredentials(String user, String password) {
            checkNotNull(user);
            checkNotNull(password);
            String basicAuthCredentials = user + ":" + password;
            return new BASE64Encoder().encode(basicAuthCredentials.getBytes());
        }

        public Builder url(String url) {
            checkNotNull(url);
            try {
                client.serverUrl = new URL(url);
                client.tlsEnabled = client.serverUrl.getProtocol().equalsIgnoreCase(HTTPS);
                return this;
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }

        public Builder basicAuth(String user, String password) {
            client.basicAuthEncoded = encodeBasicCredentials(user, password);
            return this;
        }

        public Builder proxyAuth(String user, String password) {
            client.proxyAuthEncoded = encodeBasicCredentials(user, password);
            return this;
        }

        public Builder keyStore(KeyStore keyStore) {
            checkNotNull(keyStore);
            client.keyStore = keyStore;
            return this;
        }

        public Builder keyStoreUrl(URL value) {
            checkNotNull(value);
            keyStorePath = value.getPath();
            return this;
        }

        public Builder keyStoreType(String value) {
            checkNotNull(value);
            keyStoreType = value;
            return this;
        }

        public Builder keyStorePassword(String value) {
            checkNotNull(value);
            keyStorePassword = value;
            return this;
        }

        public Builder strictHostVerification(boolean value) {
            checkNotNull(value);
            client.strictHostVerification = value;
            return this;
        }

        public Builder proxyType(Proxy.Type value) {
            checkNotNull(value);
            proxyType = value;
            return this;
        }

        public Builder proxyHost(String value) {
            checkNotNull(value);
            proxyHost = value;
            return this;
        }

        public Builder proxyPort(int value) {
            checkNotNull(value);
            proxyPort = value;
            return this;
        }

        public Builder sslContext(String value) {
            checkNotNull(value);
            client.sslContext = value;
            return this;
        }


        public SoapClient create() {
            validateAndInitKeystore();
            validateAndInitProxy();
            return client;
        }

        private void validateAndInitKeystore() {
            if (keyStorePath != null) {
                checkNotNull(keyStorePassword);
                InputStream in = null;
                try {
                    in = new FileInputStream(keyStorePath);
                    KeyStore ks = KeyStore.getInstance(keyStoreType);
                    ks.load(in, keyStorePassword.toCharArray());
                    in.close();
                    client.keyStore = ks;
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (CertificateException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (KeyStoreException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void validateAndInitProxy() {
            if (proxyType != Proxy.Type.DIRECT) {
                checkNotNull(proxyHost);
                checkNotNull(proxyPort);
                client.proxy = new Proxy(proxyType, new InetSocketAddress(proxyHost, proxyPort));
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
