/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.centeractive.ws.client.core;

import com.centeractive.ws.client.SoapClientException;
import com.centeractive.ws.client.TransmissionException;
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

import static com.centeractive.ws.client.config.SoapConstants.*;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public final class SoapClient {

    private final static Log log = LogFactory.getLog(SoapClient.class);

    private static final int INFINITE_TIMEOUT = 0;

    private URL serverUrl;
    private String basicAuthEncoded;
    private boolean tlsEnabled;
    private KeyStore keyStore;
    private boolean strictHostVerification = false;
    private Proxy proxy;
    private String proxyAuthEncoded;
    private String sslContext = SSL_CONTEXT;

    private SSLContext context;
    private HttpURLConnection connection;
    private SSLSocketFactory sslSocketFactory;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private int readTimeoutInMillis = INFINITE_TIMEOUT;
    private int connectTimeoutInMillis = INFINITE_TIMEOUT;

    private SoapClient() {
    }

    private void configureTls() {
        if (tlsEnabled == false)
            return;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
            context = SSLContext.getInstance(sslContext);
            context.init(null, new TrustManager[]{defaultTrustManager}, null);
            sslSocketFactory = context.getSocketFactory();
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            if (strictHostVerification == false) {
                ((HttpsURLConnection) connection).setHostnameVerifier(new SoapHostnameVerifier());
            }
        } catch (NoSuchAlgorithmException e) {
            throw new SoapClientException("TLS/SSL setup failed", e);
        } catch (KeyManagementException e) {
            throw new SoapClientException("TLS/SSL setup failed", e);
        } catch (KeyStoreException e) {
            throw new SoapClientException("TLS/SSL setup failed", e);
        }
    }

    private void openConnection() {
        try {
            if (proxy != null) {
                connection = (HttpURLConnection) serverUrl.openConnection(proxy);
            } else {
                connection = (HttpURLConnection) serverUrl.openConnection();
            }
        } catch (IOException e) {
            throw new SoapClientException("Connection initialization failed", e);
        }
    }

    private void configureConnection() {
        try {
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod(POST);
            connection.setConnectTimeout(connectTimeoutInMillis);
            connection.setReadTimeout(readTimeoutInMillis);
            if (basicAuthEncoded != null) {
                connection.setRequestProperty(PROP_AUTH, PROP_BASIC_AUTH + " " + basicAuthEncoded);
            }
            if (proxyAuthEncoded != null) {
                connection.setRequestProperty(PROP_PROXY_AUTH, PROP_BASIC_AUTH + " " + basicAuthEncoded);
            }
        } catch (ProtocolException e) {
            throw new SoapClientException("Connection setup failed", e);
        }

    }

    private void decorateConnectionWithSoap(String soapAction, String requestEnvelope) {
        if (requestEnvelope.contains(SOAP_1_1_NAMESPACE)) {
            if (soapAction != null) {
                connection.setRequestProperty(PROP_SOAP_ACTION_11, soapAction);
            }
            connection.setRequestProperty(PROP_CONTENT_TYPE, MIMETYPE_TEXT_XML);
        } else if (requestEnvelope.contains(SOAP_1_2_NAMESPACE)) {
            connection.setRequestProperty(PROP_CONTENT_TYPE, MIMETYPE_APPLICATION_XML);
            if (soapAction != null) {
                String prop = connection.getRequestProperty(PROP_CONTENT_TYPE);
                connection.setRequestProperty(PROP_CONTENT_TYPE, prop + PROP_DELIMITER
                        + PROP_SOAP_ACTION_12 + "\"" + soapAction + "\"");
            }

        }
        connection.setRequestProperty(PROP_CONTENT_LENGTH, Integer.toString(requestEnvelope.length()));
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
        StringBuilder errorMessage = new StringBuilder();
        int errorCode = 0;
        try {
            errorCode = connection.getResponseCode();
        } catch (IOException e) {
            // ignore
        }
        try {
            InputStream errorStream = ((HttpURLConnection) connection).getErrorStream();
            int ret = 0;
            while ((ret = errorStream.read()) > 0) {
                errorMessage.append((char) ret);
            }
            errorStream.close();
        } catch (IOException e) {
            // ignore
        } finally {
            throw new TransmissionException(errorMessage.toString(), errorCode, ex);
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

    /**
     * Underlying connection is persistent by default
     *
     * @link http://docs.oracle.com/javase/1.5.0/docs/guide/net/http-keepalive.html
     */
    public void disconnect() {
        connection.disconnect();
    }

    public String post(String requestEnvelope) {
        // TODO ugly null
        return post(null, requestEnvelope);
    }


    public String post(String soapAction, String requestEnvelope) {
        log.debug(String.format("Sending request to host=[%s] action=[%s] request:\n%s", serverUrl.toString(),
                soapAction, requestEnvelope));
        openConnection();
        configureTls();
        configureConnection();
        decorateConnectionWithSoap(soapAction, requestEnvelope);
        String response = transmit(requestEnvelope);
        log.debug("Received response:\n" + requestEnvelope);
        return response;
    }

    class SoapHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String urlHost, SSLSession sslSession) {
            return true;
        }
    }

    public static class Builder {
        SoapClient client = new SoapClient();

        private URL keyStoreUrl;
        private String keyStoreType = JKS_KEYSTORE;
        private char[] keyStorePassword;

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
                client.tlsEnabled = client.serverUrl.getProtocol().equalsIgnoreCase("https");
                return this;
            } catch (MalformedURLException ex) {
                throw new SoapClientException(String.format("URL [%s] is malformed", url), ex);
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
            keyStoreUrl = value;
            return this;
        }

        public Builder keyStoreType(String value) {
            checkNotNull(value);
            keyStoreType = value;
            return this;
        }

        public Builder keyStorePassword(String value) {
            if(value != null) {
                keyStorePassword = value.toCharArray();
            }
            return this;
        }

        public Builder strictHostVerification(boolean value) {
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
            checkArgument(value > 0);
            proxyPort = value;
            return this;
        }

        public Builder sslContext(String value) {
            checkNotNull(value);
            client.sslContext = value;
            return this;
        }

        public Builder readTimeoutInMillis(int value) {
            checkArgument(value >= 0);
            client.readTimeoutInMillis = value;
            return this;
        }

        public Builder connectTimeoutInMillis(int value) {
            checkArgument(value >= 0);
            client.connectTimeoutInMillis = value;
            return this;
        }

        public SoapClient create() {
            validateAndInitKeystore();
            validateAndInitProxy();
            return client;
        }

        private void validateAndInitKeystore() {
            if (keyStoreUrl != null) {
                try {
                    InputStream in = keyStoreUrl.openStream();
                    KeyStore ks = KeyStore.getInstance(keyStoreType);
                    ks.load(in, keyStorePassword);
                    in.close();
                    client.keyStore = ks;
                } catch (FileNotFoundException e) {
                    throw new SoapClientException("Keystore setup failed", e);
                } catch (CertificateException e) {
                    throw new SoapClientException("Keystore setup failed", e);
                } catch (NoSuchAlgorithmException e) {
                    throw new SoapClientException("Keystore setup failed", e);
                } catch (KeyStoreException e) {
                    throw new SoapClientException("Keystore setup failed", e);
                } catch (IOException e) {
                    throw new SoapClientException("Keystore setup failed", e);
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
