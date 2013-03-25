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
package org.reficio.ws.server.core;

import org.reficio.ws.server.responder.RequestResponder;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapServerFactory {

    private Integer httpPort;
    private Integer httpsPort;
    private Boolean reuseAddress;
    private Integer connectionMaxIdleTimeInSeconds;
    private Integer acceptorThreads;
    private Integer coreThreads;
    private Integer maxThreads;
    private Integer threadKeepAliveTimeInSeconds;

    private URL keyStoreUrl;
    private String keyStoreType;
    private String keyStorePassword;
    private Map<String, RequestResponder> responders;

    public void setHttpPort(Integer value) {
        checkNotNull(value);
        this.httpPort = value;
    }

    public void setHttpsPort(Integer value) {
        checkNotNull(value);
        this.httpsPort = value;
    }

    public void setConnectionMaxIdleTimeInSeconds(Integer value) {
        checkNotNull(value);
        this.connectionMaxIdleTimeInSeconds = value;
    }

    public void setAcceptorThreads(Integer value) {
        checkNotNull(value);
        this.acceptorThreads = value;
    }

    public void setCoreThreads(Integer value) {
        checkNotNull(value);
        this.coreThreads = value;
    }

    public void setMaxThreads(Integer value) {
        checkNotNull(value);
        this.maxThreads = value;
    }

    public void setThreadKeepAliveTimeInSeconds(Integer value) {
        checkNotNull(value);
        this.threadKeepAliveTimeInSeconds = value;
    }

    public void setKeyStoreUrl(URL value) {
        checkNotNull(value);
        this.keyStoreUrl = value;
    }

    public void setKeyStoreType(String value) {
        checkNotNull(value);
        this.keyStoreType = value;
    }

    public void setKeyStorePassword(String value) {
        this.keyStorePassword = value;
    }

    public void setReuseAddress(Boolean value) {
        checkNotNull(value);
        this.reuseAddress = value;
    }

    public void setResponders(Map<String, RequestResponder> responders) {
        checkNotNull(responders);
        this.responders = new HashMap<String, RequestResponder>(responders);
    }

    public SoapServer create() {
        SoapServer.Builder builder = SoapServer.builder();

        configureConnection(builder);
        configureThreadPools(builder);
        configureTimeouts(builder);
        configureKeyStore(builder);

        SoapServer server = builder.build();
        configureResponders(server);
        return server;
    }

    private void configureResponders(SoapServer server) {
        for(Map.Entry<String, RequestResponder> entry : responders.entrySet()) {
            server.registerRequestResponder(entry.getKey(), entry.getValue());
        }
    }

    private void configureThreadPools(SoapServer.Builder builder) {
        if (acceptorThreads != null) {
            builder.acceptorThreads(acceptorThreads);
        }
        if (coreThreads != null) {
            builder.coreThreads(coreThreads);
        }
        if (maxThreads != null) {
            builder.maxThreads(maxThreads);
        }
    }

    private void configureTimeouts(SoapServer.Builder builder) {
        if (connectionMaxIdleTimeInSeconds != null) {
            builder.connectionMaxIdleTimeInSeconds(connectionMaxIdleTimeInSeconds);
        }
        if (threadKeepAliveTimeInSeconds != null) {
            builder.threadKeepAliveTimeInSeconds(threadKeepAliveTimeInSeconds);
        }
    }

    private void configureConnection(SoapServer.Builder builder) {
        if (httpPort != null) {
            builder.httpPort(httpPort);
        }
        if (httpsPort != null) {
            builder.httpsPort(httpsPort);
        }
        if (reuseAddress != null) {
            builder.reuseAddress(reuseAddress);
        }
    }

    private void configureKeyStore(SoapServer.Builder builder) {
        if (keyStoreUrl != null) {
            builder.keyStoreUrl(keyStoreUrl);
        }
        if (keyStoreType != null) {
            builder.keyStoreType(keyStoreType);
        }
        builder.keyStorePassword(keyStorePassword);
    }

    public static void main(String[] args) {
        SoapServerFactory f = new SoapServerFactory();
        f.setHttpPort(9999);
        SoapServer s = f.create();

        s.start();

    }
}
