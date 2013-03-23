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
package org.reficio.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpFilter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthorizationHandler;
import org.reficio.ws.client.TransmissionException;
import org.reficio.ws.client.core.Security;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.it.util.ClientBuilder;
import org.reficio.ws.it.util.SslTunnel;
import org.reficio.ws.server.core.SoapServer;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * Test SoapServer<->SoapClient communication using HTTPS
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class HttpsProxyHttpsCooperationTest extends AbstractCooperationTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final static int PROXY_PORT = 9797;
    private final static int PROXY_SSL_PORT = 9898;

    private SslTunnel tunnel;
    private HttpProxyServer proxyServer;


    @Before
    public void initializeServer() {
        server = SoapServer.builder()
                .keyStoreUrl(getKeyStoreUrlOne())
                .keyStorePassword(getKeyStorePassword())
                .httpsPort(HOST_PORT)
                .build();
        server.start();

        tunnel = getProxyTunnel();
        tunnel.start();

        proxyServer = getProxy();
        proxyServer.start(true, true);

    }

    @After
    public void destroyServer() {
        server.stop();
        proxyServer.stop();
        tunnel.stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public HttpProxyServer getProxy() {
        Map<String, HttpFilter> filters = new HashMap<String, HttpFilter>();
        HttpProxyServer proxyServer = new DefaultHttpProxyServer(PROXY_PORT, filters, null, null, null);
        return proxyServer;
    }

    private Security proxySecurity() {
        Security securityContext = Security.builder()
                .trustStoreUrl(getKeyStoreUrlTwo())
                .trustStorePassword(getKeyStorePassword())
                .build();
        return securityContext;
    }

    private SslTunnel getProxyTunnel() {
        KeyStore ks = readKeyStore(getKeyStoreUrlTwo(), getKeyStorePassword(), "JKS");
        SslTunnel tunnel = new SslTunnel(ks, getKeyStorePassword(), PROXY_SSL_PORT, "localhost", PROXY_PORT);
        return tunnel;
    }

    private Security endpointSecurity() {
        Security securityContext = Security.builder()
                .trustStoreUrl(getKeyStoreUrlOne())
                .trustStorePassword(getKeyStorePassword())
                .build();
        return securityContext;
    }

//    @Test
//    public void testService1_httpProxy_defaultProxySetting_noProxy() throws Exception {
//        verifyServiceBehavior(1, new ClientBuilder() {
//            @Override
//            public SoapClient buildClient(String endpointUrl) {
//                return SoapClient.builder().endpointUri("https://" + endpointUrl)
//                        .endpointSecurity(endpointSecurity())
//                        .build();
//            }
//        });
//    }

    @Test
    public void testService1_httpProxy_noAuthentication() throws Exception {
        verifyServiceBehavior(1, new ClientBuilder() {
            @Override
            public SoapClient buildClient(String endpointUrl) {
                return SoapClient.builder().endpointUri("https://" + endpointUrl)
                        .endpointSecurity(endpointSecurity())
                        .proxyUri("https://127.0.0.1:" + PROXY_SSL_PORT)
                        .proxySecurity(proxySecurity())
                        .build();
            }
        });
    }

    @Test
    public void testService1_httpsProxy_basicAuthentication_success() throws Exception {
        proxyServer.addProxyAuthenticationHandler(new ProxyAuthorizationHandler() {
            @Override
            public boolean authenticate(String user, String pass) {
                return user.equals("tom") && pass.equals("007");
            }
        });

        final Security securityContext = Security.builder()
                .trustStoreUrl(getKeyStoreUrlTwo())
                .trustStorePassword(getKeyStorePassword())
                .authBasic("tom", "007")
                .build();

        verifyServiceBehavior(1, new ClientBuilder() {
            @Override
            public SoapClient buildClient(String endpointUrl) {
                return SoapClient.builder().endpointUri("https://" + endpointUrl)
                        .endpointSecurity(endpointSecurity())
                        .proxyUri("https://127.0.0.1:" + PROXY_SSL_PORT)
                        .proxySecurity(securityContext)
                        .build();
            }
        });
    }


    @Test
    public void testService1_httpsProxy_basicAuthentication_failure() throws Exception {

        exception.expect(TransmissionException.class);
        exception.expectMessage("[407]");

        proxyServer.addProxyAuthenticationHandler(new ProxyAuthorizationHandler() {
            @Override
            public boolean authenticate(String user, String pass) {
                return user.equals("tom") && pass.equals("007");
            }
        });

        final Security securityContext = Security.builder()
                .trustStoreUrl(getKeyStoreUrlTwo())
                .trustStorePassword(getKeyStorePassword())
                .authBasic("james", "003")
                .build();

        verifyServiceBehavior(1, new ClientBuilder() {
            @Override
            public SoapClient buildClient(String endpointUrl) {
                return SoapClient.builder().endpointUri("https://" + endpointUrl)
                        .endpointSecurity(endpointSecurity())
                        .proxyUri("https://127.0.0.1:" + PROXY_SSL_PORT)
                        .proxySecurity(securityContext)
                        .build();
            }
        });
    }

}
