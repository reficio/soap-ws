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
import org.junit.Test;
import org.reficio.ws.SoapException;
import org.reficio.ws.client.InternalServerException;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.server.core.SoapServer;
import org.reficio.ws.server.responder.RequestResponder;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.Source;

import static junit.framework.Assert.assertNotNull;

/**
 * Test http status 500 handling
 *
 * @author Szymon.Kolorz
 */
public class HttpStatus500Test extends AbstractCooperationTest {
    private static final String CONTEXT_PATH = "/endpoint500";
    private static final String VALID_REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"/>";

    @Test
    public void HttpStatus500Test() {
        SoapClient client = new ClientBuilderImpl().buildClient(String.format("%s:%s%s", HOST_URL, HOST_PORT, CONTEXT_PATH));
        InternalServerException expected = null;

        try {
            client.post(VALID_REQUEST);
        } catch (InternalServerException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertNotNull(expected.getEntity());
    }

    @Before
    public void initializeServer() {
        server = SoapServer.builder()
                .httpPort(HOST_PORT)
                .build();

        RequestResponder alwaysFailingResponder = new RequestResponder() {
            @Override
            public Source respond(SoapMessage request) {
                throw new SoapException("sorry, this endpoint always fails");
            }
        };
        server.registerRequestResponder(CONTEXT_PATH, alwaysFailingResponder);
        server.start();
    }

    @After
    public void destroyServer() {
        server.stop();
    }
}
