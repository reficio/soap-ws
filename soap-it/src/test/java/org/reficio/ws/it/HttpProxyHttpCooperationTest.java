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
package org.reficio.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthorizationHandler;
import org.reficio.ws.client.TransmissionException;
import org.reficio.ws.client.core.Security;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.it.util.ClientBuilder;
import org.reficio.ws.server.core.SoapServer;

/**
 * Test SoapServer<->SoapClient communication using HTTPS
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class HttpProxyHttpCooperationTest extends AbstractCooperationTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final static int PROXY_PORT = 9797;

    @Before
    public void initializeServer() {
        server = SoapServer.builder()
                .httpPort(HOST_PORT)
                .build();
        server.start();
    }

    @After
    public void destroyServer() {
        server.stop();
    }

    public HttpProxyServer initProxy() {
        HttpProxyServer proxyServer = new DefaultHttpProxyServer(PROXY_PORT);
        proxyServer.start(true, true);
        return proxyServer;
    }

    @Test
    public void testService1_httpProxy_defaultProxySetting() throws Exception {
        HttpProxyServer proxyServer = initProxy();
        try {
            verifyServiceBehavior(1, new ClientBuilder() {
                @Override
                public SoapClient buildClient(String endpointUrl) {
                    return SoapClient.builder().endpointUri("http://" + endpointUrl)
                            .build();
                }
            });
        } finally {
            proxyServer.stop();
        }
    }

    @Test
    public void testService1_httpProxy_directProxy() throws Exception {
        HttpProxyServer proxyServer = initProxy();
        try {
            verifyServiceBehavior(1, new ClientBuilder() {
                @Override
                public SoapClient buildClient(String endpointUrl) {
                    return SoapClient.builder().endpointUri("http://" + endpointUrl)
                            .build();
                }
            });
        } finally {
            proxyServer.stop();
        }
    }

    @Test
    public void testService1_httpProxy_noAuthentication() throws Exception {
        HttpProxyServer proxyServer = initProxy();
        try {
            verifyServiceBehavior(1, new ClientBuilder() {
                @Override
                public SoapClient buildClient(String endpointUrl) {
                    return SoapClient.builder().endpointUri("http://" + endpointUrl)
                            .proxyUri("http://127.0.0.1:" + PROXY_PORT)
                            .build();
                }
            });
        } finally {
            proxyServer.stop();
        }
    }

    @Test
    public void testService1_httpProxy_basicAuthentication_success() throws Exception {
        HttpProxyServer proxyServer = initProxy();
        proxyServer.addProxyAuthenticationHandler(new ProxyAuthorizationHandler() {
            @Override
            public boolean authenticate(String user, String pass) {
                return user.equals("tom") && pass.equals("007");
            }
        });
        try {

            final Security security = Security.builder()
                    .authBasic("tom", "007")
                    .build();

            verifyServiceBehavior(1, new ClientBuilder() {
                @Override
                public SoapClient buildClient(String endpointUrl) {
                    return SoapClient.builder()
                            .endpointUri("http://" + endpointUrl)
                            .proxyUri("http://127.0.0.1:" + PROXY_PORT)
                            .proxySecurity(security)
                            .build();
                }
            });
        } finally {
            proxyServer.stop();
        }
    }

    @Test
    public void testService1_httpProxy_basicAuthentication_failure() throws Exception {

        exception.expect(TransmissionException.class);
        exception.expectMessage("[407]");

        HttpProxyServer proxyServer = initProxy();

        proxyServer.addProxyAuthenticationHandler(new ProxyAuthorizationHandler() {
            @Override
            public boolean authenticate(String user, String pass) {
                return user.equals("tom") && pass.equals("007");
            }
        });

        final Security props = Security.builder()
                .authBasic("james", "003")
                .build();

        try {
            verifyServiceBehavior(1, new ClientBuilder() {
                @Override
                public SoapClient buildClient(String endpointUrl) {
                    return SoapClient.builder().endpointUri("http://" + endpointUrl)
                            .proxyUri("http://127.0.0.1:" + PROXY_PORT)
                            .proxySecurity(props)
                            .build();
                }
            });
        } finally {
            proxyServer.stop();
        }
    }

}
