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

import com.centeractive.ws.client.core.SoapClient;
import com.centeractive.ws.server.core.SoapServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 22/11/11
 * Time: 8:21 PM
 */
public class TrickyCooperationTest extends AbstractCooperationTest {

    private final static Log log = LogFactory.getLog(PlainCooperationTest.class);

    @Before
    public void initializeServer() {
        server = SoapServer.builder()
                .httpPort(HOST_PORT)
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
        SoapClient client = SoapClient.builder().url("http://" + endpointUrl).create();
        try {
            String response = null;
            for (int i = 0; i < 30; i++) {
                response = client.post(soapAction, request);
            }
            return response;
        } finally {
            client.disconnect();
        }
    }

    @Test
    public void testMultipleRequests() throws Exception {
        verifyServiceBehavior(1);
    }

}