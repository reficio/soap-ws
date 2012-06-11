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
package com.centeractive.ws.server;

import com.centeractive.ws.builder.utils.ResourceUtils;
import com.centeractive.ws.client.core.SoapClient;
import com.centeractive.ws.server.core.SoapServer;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

import java.net.URL;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 19/11/11
 * Time: 6:17 PM
 */
public class HttpsCooperationTest extends AbstractCooperationTest {

    private final static Log log = LogFactory.getLog(HttpsCooperationTest.class);

    protected URL getTestKeyStoreUrl() {
        return ResourceUtils.getResourceWithAbsolutePackagePath("/keystores/my dir", ".keystore");
    }

    protected String getTestKeyStorePassword() {
        return "changeit";
    }

    @Before
    public void initializeServer() {
        server = SoapServer.builder()
                .keyStoreUrl(getTestKeyStoreUrl())
                .keyStorePassword(getTestKeyStorePassword())
                .httpsPort(HOST_PORT)
                .create();
        server.start();
    }

    @After
    public void destroyServer() {
        server.stop();
    }

    public String postRequest(String endpointUrl, String request) {
        return postRequest(endpointUrl, request, null);
    }

    @Override
    protected String postRequest(String endpointUrl, String request, String soapAction) {
        SoapClient client = SoapClient.builder()
                .url("https://" + endpointUrl)
                .keyStoreUrl(getTestKeyStoreUrl())
                .keyStorePassword(getTestKeyStorePassword())
                .create();
        return client.post(soapAction, request);
    }

    @Test
    public void testService1() throws Exception {
        verifyServiceBehavior(1);
    }

    @Test
    public void testService2() throws Exception {
        verifyServiceBehavior(2);
    }

}
