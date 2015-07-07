/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.ws.client.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.util.EntityUtils;
import org.reficio.ws.SoapException;
import org.reficio.ws.annotation.ThreadSafe;
import org.reficio.ws.client.SoapClientException;
import org.reficio.ws.client.TransmissionException;
import org.reficio.ws.client.ssl.SSLUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.reficio.ws.client.core.SoapConstants.*;

/**
 * SOAP client enables the user to communicate with a SOAP server on a purely XML level.
 * It supports SSL/TLS, basic-authentication and java.net.Proxy.
 * When it comes to SOAP it supports version 1.1 and 1.2 - SOAPAction attribute is automatically properly placed,
 * either in the header (SOAP 1.1) or in the content (SOAP 1.2).
 * SOAP version recognition is based on the SOAP namespace included in the payload.
 * This class may throw an unchecked @see org.reficio.ws.client.SoapClientException
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
@ThreadSafe
public final class SoapClient {

    private final static Log log = LogFactory.getLog(SoapClient.class);

    private final static String NULL_SOAP_ACTION = null;

    private int readTimeoutInMillis;
    private int connectTimeoutInMillis;

    private URI endpointUri;
    private Security endpointProperties;
    private boolean endpointTlsEnabled;

    private URI proxyUri;
    private Security proxyProperties;
    private boolean proxyTlsEnabled;

    private List<Header> headers;
    private Charset charset;

    private CloseableHttpClient client;
    private RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistry;

    // ----------------------------------------------------------------
    // PUBLIC API
    // ----------------------------------------------------------------

    /**
     * Post the SOAP message to the SOAP server without specifying the SOAPAction
     *
     * @param requestEnvelope SOAP message envelope
     * @return The result returned by the SOAP server
     */
    public String post(String requestEnvelope) {
        return post(NULL_SOAP_ACTION, requestEnvelope);
    }

    /**
     * Post the SOAP message to the SOAP server specifying the SOAPAction
     *
     * @param soapAction      SOAPAction attribute
     * @param requestEnvelope SOAP message envelope
     * @return The result returned by the SOAP server
     */
    public String post(String soapAction, String requestEnvelope) {
        log.debug(String.format("Sending request to host=[%s] action=[%s] request:%n%s", endpointUri.toString(),
                soapAction, requestEnvelope));
        String response = transmit(soapAction, requestEnvelope);
        log.debug("Received response:\n" + requestEnvelope);
        return response;
    }

    /**
     * Disconnects from the SOAP server
     * Underlying connection is a persistent connection by default:
     *
     * @link http://docs.oracle.com/javase/1.5.0/docs/guide/net/http-keepalive.html
     */
    public void disconnect() {
        if (client != null) {
            client.getConnectionManager().shutdown();
        }
    }

    // ----------------------------------------------------------------
    // TRANSMISSION API
    // ----------------------------------------------------------------
    private HttpPost generatePost(String soapAction, String requestEnvelope) {
        HttpPost post = new HttpPost(endpointUri.toString());
        if (charset == null) {
            charset = Charset.forName("UTF-8");
        }
        StringEntity contentEntity = new StringEntity(requestEnvelope, charset);
        post.setEntity(contentEntity);
        if (headers != null) {
            for (Header header : headers) {
                post.addHeader(header);
            }
        }
        if (requestEnvelope.contains(SOAP_1_1_NAMESPACE)) {
            soapAction = soapAction != null ? "\"" + soapAction + "\"" : "";
            if (post.getFirstHeader(PROP_SOAP_ACTION_11) == null) {
                post.addHeader(PROP_SOAP_ACTION_11, soapAction);
            }
            if (post.getFirstHeader(PROP_CONTENT_TYPE) == null) {
                post.addHeader(PROP_CONTENT_TYPE, MIMETYPE_TEXT_XML);
            }
            post.addHeader(PROP_CONTENT_TYPE, MIMETYPE_TEXT_XML);
//            client.getParams().setParameter(PROP_CONTENT_TYPE, MIMETYPE_TEXT_XML);
        } else if (requestEnvelope.contains(SOAP_1_2_NAMESPACE)) {
            if (post.getFirstHeader(PROP_CONTENT_TYPE) == null) {
                String contentType = MIMETYPE_APPLICATION_XML;
                if (soapAction != null) {
                    contentType = contentType + PROP_DELIMITER + PROP_SOAP_ACTION_12 + "\"" + soapAction + "\"";
                }
                post.addHeader(PROP_CONTENT_TYPE, contentType);
            } else {
                Header header = post.getFirstHeader(PROP_CONTENT_TYPE);
                String sHeader = header.getValue() + PROP_DELIMITER + PROP_SOAP_ACTION_12 + "\"" + soapAction + "\"";
                post.removeHeader(header);
                post.addHeader(PROP_CONTENT_TYPE, sHeader);
            }
        }
        return post;
    }

    private String transmit(String soapAction, String data) {
        HttpPost post = generatePost(soapAction, data);
        return executePost(post);
    }

    private String executePost(HttpPost post) {
        try {
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                throw new TransmissionException(statusLine.getReasonPhrase(), statusLine.getStatusCode());
            }
            return entity == null ? null : EntityUtils.toString(entity);
        } catch (SoapException ex) {
            throw ex;
        } catch (ConnectTimeoutException ex) {
            throw new TransmissionException("Connection timed out", ex);
        } catch (IOException ex) {
            throw new TransmissionException("Transmission failed", ex);
        } catch (RuntimeException ex) {
            post.abort();
            throw new TransmissionException("Transmission aborted", ex);
        }
    }

    // ----------------------------------------------------------------
    // INITIALIZATION API
    // ----------------------------------------------------------------
    private void initialize() {
        configureClientContext();
        configureClient();
        configureRequestConfig();
        configureAuthentication();
        SSLConnectionSocketFactory factory = configureTls();
        clientContext.setRequestConfig(requestConfig.build());
        configureProxy();
        if (factory != null) {
            socketFactoryRegistry = RegistryBuilder.create();
            socketFactoryRegistry.register(HTTPS, factory);

            connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry.build());
        } else {
            connectionManager = new PoolingHttpClientConnectionManager();
        }
        builder.setConnectionManager(connectionManager);
        client = builder.build();
    }

    HttpClientConnectionManager connectionManager;

    HttpClientContext clientContext;

    private HttpClientContext configureClientContext() {
        clientContext = HttpClientContext.create();
        return clientContext;
    }

    RequestConfig.Builder requestConfig;

    private RequestConfig.Builder configureRequestConfig() {
        if (requestConfig == null) {
            requestConfig = RequestConfig.custom();
        }
        return requestConfig.setConnectionRequestTimeout(connectTimeoutInMillis)
                .setSocketTimeout(readTimeoutInMillis);
    }

    HttpClientBuilder builder;

    private HttpClientBuilder configureClient() {
        if (this.builder == null) {
            builder = HttpClientBuilder.create();
        }
        return builder;
    }

    private HttpClientBuilder configureAuthentication() {
        configureAuthentication(endpointUri, endpointProperties);
        configureAuthentication(proxyUri, proxyProperties);
        return builder;
    }

    private HttpClientBuilder configureAuthentication(URI uri, Security security) {
        if (this.builder == null) {
            builder = HttpClientBuilder.create();
        }
        if (security.isAuthEnabled()) {
            AuthScope scope = new AuthScope(uri.getHost(), uri.getPort());
            Credentials credentials = null;
            if (security.isAuthBasic()) {
                credentials = new UsernamePasswordCredentials(security.getAuthUsername(), security.getAuthPassword());
            } else if (security.isAuthDigest()) {
                credentials = new UsernamePasswordCredentials(security.getAuthUsername(), security.getAuthPassword());
            } else if (security.isAuthNtlm()) {
                // TODO
                credentials = new NTCredentials(security.getAuthUsername(), security.getAuthPassword(), null, null);
            } else if (security.isAuthSpnego()) {
                // TODO
            }
            CredentialsProvider provider = new SystemDefaultCredentialsProvider();
            provider.setCredentials(scope, credentials);
            builder.setDefaultCredentialsProvider(provider);
        }
        return builder;
    }

    private SSLConnectionSocketFactory configureTls() {
//        SSLSocketFactory factory;
        SSLConnectionSocketFactory factory = null;
        int port;
        try {
            if (endpointTlsEnabled && proxyTlsEnabled) {
                factory = SSLUtils.getMergedSocketFactory(endpointProperties, proxyProperties);
                registerTlsScheme(factory, proxyUri.getPort());
            } else if (endpointTlsEnabled) {
                factory = SSLUtils.getFactory(endpointProperties);
                port = endpointUri.getPort();
                registerTlsScheme(factory, port);
            } else if (proxyTlsEnabled) {
                factory = SSLUtils.getFactory(proxyProperties);
                port = proxyUri.getPort();
                registerTlsScheme(factory, port);
            }
        } catch (GeneralSecurityException ex) {
            throw new SoapClientException(ex);
        }
        return factory;
    }


    private void registerTlsScheme(LayeredConnectionSocketFactory factory, int port) {
        builder.setSchemePortResolver(DefaultSchemePortResolver.INSTANCE);
//        Scheme sch = new Scheme(HTTPS, port, factory);
//        client.getConnectionManager().getSchemeRegistry().register(sch);
        builder.setSSLSocketFactory(factory);
    }

    private void configureProxy() {
        if (proxyUri == null) {
            return;
        }
        if (proxyTlsEnabled) {
            final HttpHost proxy = new HttpHost(proxyUri.getHost(), proxyUri.getPort(), HTTPS);
            // https://issues.apache.org/jira/browse/HTTPCLIENT-1318
            // http://stackoverflow.com/questions/15048102/httprouteplanner-how-does-it-work-with-an-https-proxy
            // To make the HttpClient talk to a HTTP End-site through an HTTPS Proxy, the route should be secure,
            //  but there should not be any Tunnelling or Layering.
            if (!endpointTlsEnabled) {
                builder.setRoutePlanner(new HttpRoutePlanner() {
                    @Override
                    public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) {
                        return new HttpRoute(target, null, proxy, true, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
                    }
                });
            }

            requestConfig.setProxy(proxy);
        } else {
            HttpHost proxy = new HttpHost(proxyUri.getHost(), proxyUri.getPort());
            requestConfig.setProxy(proxy);
        }
    }


    // ----------------------------------------------------------------
    // BUILDER API
    // ----------------------------------------------------------------
    private SoapClient() {
    }

    /**
     * Builder to construct a properly populated SoapClient
     */
    public static class Builder {

        private Integer readTimeoutInMillis = INFINITE_TIMEOUT;
        private Integer connectTimeoutInMillis = INFINITE_TIMEOUT;

        private URI endpointUri;
        private Security endpointProperties;
        private boolean endpointTlsEnabled;

        private URI proxyUri;
        private Security proxyProperties;
        private boolean proxyTlsEnabled;

        private List<Header> headers;
        private Charset charset;

        /**
         * @param header Header to add to request. Null is not accepted.
         * @return builder
         */
        public Builder header(Header header) {
            checkNotNull(header);
            if (this.headers == null) {
                this.headers = new ArrayList<Header>();
            }
            this.headers.add(header);
            return this;
        }

        /**
         * @param headers List<Header> of headers to add to request. Null is not accepted.
         * @return builder
         */
        public Builder headers(List<Header> headers) {
            checkNotNull(headers);
            if (this.headers == null) {
                this.headers = new ArrayList<Header>();
            }
            this.headers.addAll(headers);
            return this;
        }

        /**
         * @param value String representation of the charset of the request. Null is not accepted.
         * @return builder
         */
        public Builder charset(String value) {
            checkNotNull(value);
            return charset(Charset.forName(value));
        }

        /**
         * @param value Charset of the request. Null is not accepted.
         * @return builder
         */
        public Builder charset(Charset value) {
            checkNotNull(value);
            this.charset = value;
            return this;
        }

        /**
         * @param value URL of the SOAP endpoint to whom the client should send messages. Null is not accepted.
         * @return builder
         */
        public Builder endpointUri(String value) {
            checkNotNull(value);
            try {
                URI uri = new URI(value);
                return endpointUri(uri);
            } catch (URISyntaxException ex) {
                throw new SoapClientException(String.format("URI [%s] is malformed", value), ex);
            }
        }

        /**
         * @param value URL of the SOAP endpoint to whom the client should send messages. Null is not accepted.
         * @return builder
         */
        public Builder endpointUri(URI value) {
            endpointUri = checkNotNull(value);
            endpointTlsEnabled = value.getScheme().equalsIgnoreCase(HTTPS);
            return this;
        }

        /**
         * @param value URL of the SOAP endpoint to whom the client should send messages. Null is not accepted.
         * @return builder
         */
        public Builder proxyUri(String value) {
            checkNotNull(value);
            try {
                URI uri = new URI(value);
                return proxyUri(uri);
            } catch (URISyntaxException ex) {
                throw new SoapClientException(String.format("URI [%s] is malformed", value), ex);
            }
        }

        /**
         * @param value URL of the SOAP endpoint to whom the client should send messages. Null is not accepted.
         * @return builder
         */
        public Builder proxyUri(URI value) {
            proxyUri = checkNotNull(value);
            proxyTlsEnabled = value.getScheme().equalsIgnoreCase(HTTPS);
            return this;
        }

        public Builder endpointSecurity(Security value) {
            this.endpointProperties = checkNotNull(value);
            return this;
        }

        public Builder proxySecurity(Security value) {
            this.proxyProperties = checkNotNull(value);
            return this;
        }

        /**
         * @param value Specifies the timeout in millisecond for the read operation. Has to be not negative.
         * @return builder
         */
        public Builder readTimeoutInMillis(int value) {
            checkArgument(value >= 0);
            readTimeoutInMillis = value;
            return this;
        }

        /**
         * @param value Specifies the timeout in millisecond for the connect operation. Has to be not negative.
         * @return builder
         */
        public Builder connectTimeoutInMillis(int value) {
            checkArgument(value >= 0);
            connectTimeoutInMillis = value;
            return this;
        }

        /**
         * Constructs properly populated soap client
         *
         * @return properly populated soap clients
         */
        public SoapClient build() {
            return initializeClient();
        }

        private SoapClient initializeClient() {
            SoapClient client = new SoapClient();
            client.endpointUri = endpointUri;
            if (endpointProperties == null) {
                endpointProperties = Security.builder().build();
            }
            client.endpointProperties = endpointProperties;
            client.endpointTlsEnabled = endpointTlsEnabled;

            client.proxyUri = proxyUri;
            if (proxyProperties == null) {
                proxyProperties = Security.builder().build();
            }
            client.proxyProperties = proxyProperties;
            client.proxyTlsEnabled = proxyTlsEnabled;

            client.readTimeoutInMillis = readTimeoutInMillis;
            client.connectTimeoutInMillis = connectTimeoutInMillis;

            if (headers != null) {
                client.headers = headers;
            }
            if (charset != null) {
                client.charset = charset;
            }

            client.initialize();
            return client;
        }
    }

    /**
     * @return a new instance of a SoapClient Builder
     */
    public static Builder builder() {
        return new Builder();
    }

}
