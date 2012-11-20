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

import java.net.Proxy;
import java.security.KeyStore;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapClientFactory {

    // has to be guard by validation flag if set or not
    private boolean basicAuth;
    private String basicAuthUser;
    private String basicAuthPassword;

    // has to be guard by validation flag if set or not
    private boolean proxyBasicAuth;
    private String proxyBasicAuthUser;
    private String proxyBasicAuthPassword;

    // will be validated as a group later in the builder
    private KeyStore trustStore;
    private String trustStoreUrl;
    private String trustStoreType;
    private String trustStorePassword;

    // will be validated as a group later in the builder
    private Proxy.Type proxyType;
    private String proxyHost;
    private Integer proxyPort;

    // will be validated separately
    private String endpointUrl;
    private Boolean strictHostVerification;
    private String sslContextProtocol;
    private Integer readTimeoutInMillis;
    private Integer connectTimeoutInMillis;

    public void setBasicAuthUser(String basicAuthUser) {
        checkNotNull(basicAuthUser);
        this.basicAuth = true;
        this.basicAuthUser = basicAuthUser;
    }

    public void setBasicAuthPassword(String basicAuthPassword) {
        checkNotNull(basicAuthPassword);
        this.basicAuth = true;
        this.basicAuthPassword = basicAuthPassword;
    }

    public void setProxyBasicAuthUser(String proxyBasicAuthUser) {
        checkNotNull(proxyBasicAuthUser);
        this.proxyBasicAuth = true;
        this.proxyBasicAuthUser = proxyBasicAuthUser;
    }

    public void setProxyBasicAuthPassword(String proxyBasicAuthPassword) {
        checkNotNull(proxyBasicAuthPassword);
        this.proxyBasicAuth = true;
        this.proxyBasicAuthPassword = proxyBasicAuthPassword;
    }

    public void setTrustStore(KeyStore trustStore) {
        checkNotNull(trustStore);
        this.trustStore = trustStore;
    }

    public void setTrustStoreUrl(String trustStoreUrl) {
        checkNotNull(trustStoreUrl);
        this.trustStoreUrl = trustStoreUrl;
    }

    public void setTrustStoreType(String trustStoreType) {
        checkNotNull(trustStoreType);
        this.trustStoreType = trustStoreType;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        // NULL is accepted
        this.trustStorePassword = trustStorePassword;
    }


    public void setProxyType(String proxyType) {
        checkNotNull(proxyType);
        this.proxyType = Proxy.Type.valueOf(proxyType);
    }

    public void setProxyHost(String proxyHost) {
        checkNotNull(proxyHost);
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(Integer proxyPort) {
        checkNotNull(proxyPort);
        this.proxyPort = proxyPort;
    }

    public void setEndpointUrl(String endpointUrl) {
        checkNotNull(endpointUrl);
        this.endpointUrl = endpointUrl;
    }

    public void setStrictHostVerification(Boolean strictHostVerification) {
        checkNotNull(strictHostVerification);
        this.strictHostVerification = strictHostVerification;
    }

    public void setSslContextProtocol(String sslContextProtocol) {
        checkNotNull(sslContextProtocol);
        this.sslContextProtocol = sslContextProtocol;
    }

    public void setReadTimeoutInMillis(Integer readTimeoutInMillis) {
        checkNotNull(readTimeoutInMillis);
        this.readTimeoutInMillis = readTimeoutInMillis;
    }

    public void setConnectTimeoutInMillis(Integer connectTimeoutInMillis) {
        checkNotNull(connectTimeoutInMillis);
        this.connectTimeoutInMillis = connectTimeoutInMillis;
    }

    public SoapClient create() {
        SoapClient.Builder builder = SoapClient.builder();

        configureConnection(builder);
        configureTrustStore(builder);
        configureSecurity(builder);
        configureProxy(builder);

        return builder.build();
    }

    private void configureConnection(SoapClient.Builder builder) {
        if (endpointUrl != null) {
            builder.endpointUrl(endpointUrl);
        }
        if (readTimeoutInMillis != null) {
            builder.readTimeoutInMillis(readTimeoutInMillis);
        }
        if (connectTimeoutInMillis != null) {
            builder.connectTimeoutInMillis(connectTimeoutInMillis);
        }
    }

    private void configureSecurity(SoapClient.Builder builder) {
        if (basicAuth) {
            builder.basicAuth(basicAuthUser, basicAuthPassword);
        }
        if (strictHostVerification != null) {
            builder.strictHostVerification(strictHostVerification);
        }
        if (sslContextProtocol != null) {
            builder.sslContextProtocol(sslContextProtocol);
        }
    }

    private void configureProxy(SoapClient.Builder builder) {
        if (proxyHost != null) {
            builder.proxyHost(proxyHost);
        }
        if (proxyPort != null) {
            builder.proxyPort(proxyPort);
        }
        if (proxyType != null) {
            builder.proxyType(proxyType);
        }
        if (proxyBasicAuth) {
            builder.proxyBasicAuth(proxyBasicAuthUser, proxyBasicAuthPassword);
        }
    }

    private void configureTrustStore(SoapClient.Builder builder) {
        if (trustStore != null) {
            builder.trustStore(trustStore);
        }
        if (trustStoreUrl != null) {
            builder.trustStoreUrl(trustStoreUrl);
        }
        if (trustStoreType != null) {
            builder.trustStoreType(trustStoreType);
        }
        // trustStorePassword may be null
        builder.trustStorePassword(trustStorePassword);
    }

}
