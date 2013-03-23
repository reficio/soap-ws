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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.reficio.ws.client.SoapClientException;
import org.reficio.ws.client.TransmissionException;
import org.reficio.ws.client.core.Security;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.it.util.ClientBuilder;
import org.reficio.ws.server.core.SoapServer;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

/**
 * Test SoapServer<->SoapClient communication using HTTPS
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
@RunWith(Parameterized.class)
public class HttpsCooperationTest extends AbstractCooperationTest {

    private URL keyStoreUrl;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public HttpsCooperationTest(URL url) {
        keyStoreUrl = url;
    }

    @Parameterized.Parameters
    public static Collection keyStores() {
        return Arrays.asList(new Object[][]{{getKeyStoreUrlOne()}, {getMultiKeyStoreUrl()}});
    }

    @Before
    public void initializeServer() {
        server = SoapServer.builder()
                .keyStoreUrl(keyStoreUrl)
                .keyStorePassword(getKeyStorePassword())
                .httpsPort(HOST_PORT)
                .build();
        server.start();
    }

    @After
    public void destroyServer() {
        server.stop();
    }

    @Test
    public void testService1() throws Exception {
        verifyServiceBehavior(1, client());
    }

    @Test
    public void testService2() throws Exception {
        verifyServiceBehavior(2, client());
    }

    @Test
    public void testService2_wrongKeyStore_failure() throws Exception {

        exception.expect(SoapClientException.class);
        exception.expectMessage("not authenticated");

        verifyServiceBehavior(2, new ClientBuilder() {
            @Override
            public SoapClient buildClient(String endpointUrl) {
                Security securityContext = Security.builder()
                        .trustStoreUrl(getKeyStoreUrlTwo())
                        .trustStorePassword(getKeyStorePassword())
                        .build();

                return SoapClient.builder().endpointUri("https://" + endpointUrl)
                        .endpointSecurity(securityContext)
                        .build();
            }
        });
    }

    private class HttpsClientBuilder implements ClientBuilder {
        @Override
        public SoapClient buildClient(String endpointUrl) {
            Security securityContext = Security.builder()
                    .trustStoreUrl(keyStoreUrl)
                    .trustStorePassword(getKeyStorePassword())
                    .build();

            return SoapClient.builder().endpointUri("https://" + endpointUrl)
                    .endpointSecurity(securityContext)
                    .build();
        }
    }

    private ClientBuilder client() {
        return new HttpsClientBuilder();
    }

}
