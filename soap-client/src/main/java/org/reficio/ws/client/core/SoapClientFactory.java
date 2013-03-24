/**
 * Copyright (c) 2012 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapClientFactory {

    private String endpointUri;
    private Security endpointSecurity;

    private String proxyUri;
    private Security proxySecurity;

    private Integer readTimeoutInMillis;
    private Integer connectTimeoutInMillis;

    public String getEndpointUri() {
        return endpointUri;
    }

    public void setEndpointUri(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public String getProxyUri() {
        return proxyUri;
    }

    public void setProxyUri(String proxyUri) {
        this.proxyUri = proxyUri;
    }

    public Integer getReadTimeoutInMillis() {
        return readTimeoutInMillis;
    }

    public void setReadTimeoutInMillis(Integer readTimeoutInMillis) {
        this.readTimeoutInMillis = readTimeoutInMillis;
    }

    public Integer getConnectTimeoutInMillis() {
        return connectTimeoutInMillis;
    }

    public void setConnectTimeoutInMillis(Integer connectTimeoutInMillis) {
        this.connectTimeoutInMillis = connectTimeoutInMillis;
    }

    public Security getEndpointSecurity() {
        return endpointSecurity;
    }

    public void setEndpointSecurity(Security endpointSecurity) {
        this.endpointSecurity = endpointSecurity;
    }

    public Security getProxySecurity() {
        return proxySecurity;
    }

    public void setProxySecurity(Security proxySecurity) {
        this.proxySecurity = proxySecurity;
    }

    public SoapClient create() {
        SoapClient.Builder builder = SoapClient.builder();

        if (endpointUri != null) {
            builder.endpointUri(endpointUri);
        }
        if (proxyUri != null) {
            builder.proxyUri(proxyUri);
        }
        if (readTimeoutInMillis != null) {
            builder.readTimeoutInMillis(readTimeoutInMillis);
        }
        if (connectTimeoutInMillis != null) {
            builder.connectTimeoutInMillis(connectTimeoutInMillis);
        }
        if (endpointSecurity != null) {
            builder.endpointSecurity(endpointSecurity);
        }
        if (proxySecurity != null) {
            builder.proxySecurity(proxySecurity);
        }
        return builder.build();
    }

}
