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
import org.reficio.ws.client.InternalServerException;
import org.reficio.ws.server.core.SoapServer;

import static junit.framework.Assert.assertNotNull;

/**
 * Test SoapServer<->SoapClient communication using plain HTTP connection
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class HttpCooperationTest extends AbstractCooperationTest {

    private final static boolean SKIP_SOAP_ACTION = false;
    private final static boolean POST_SOAP_ACTION = true;

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

    @Test
    public void testService1() throws Exception {
        verifyServiceBehavior(1);
    }

    @Test
    public void testService2() throws Exception {
        verifyServiceBehavior(2);
    }

    @Test
    public void testService3() throws Exception {
        verifyServiceBehavior(3);
    }

    @Test
    public void testService4() throws Exception {
        verifyServiceBehavior(4);
    }

    @Test
    public void testService5() throws Exception {
        verifyServiceBehavior(5);
    }

    @Test
    public void testService6() throws Exception {
        verifyServiceBehavior(6);
    }

    @Test
    public void testService7() throws Exception {
        verifyServiceBehavior(7);
    }

    @Test
    public void testService8() throws Exception {
        verifyServiceBehavior(8);
    }

    @Test
    public void testService9() throws Exception {
        verifyServiceBehavior(9);
    }

    @Test
    public void testService10() throws Exception {
        verifyServiceBehavior(10);
    }

    @Test
    public void testService11() throws Exception {
        verifyServiceBehavior(11);
    }

    @Test
    public void testService12() throws Exception {
        verifyServiceBehavior(12);
    }

    @Test
    public void testService13() throws Exception {
        verifyServiceBehavior(13);
    }

    @Test
    public void testService14() throws Exception {
        verifyServiceBehavior(14);
    }

    @Test
    public void testService15_noSoapAction() throws Exception {
        InternalServerException expected = null;
        try {
            verifyServiceBehavior(15, SKIP_SOAP_ACTION);
        } catch (InternalServerException ex) {
            expected = ex;
        }
        assertNotNull(expected);
    }

    @Test
    public void testService15_withSoapAction() throws Exception {
        verifyServiceBehavior(15, POST_SOAP_ACTION);
    }

    @Test
    public void testService16() throws Exception {
        verifyServiceBehavior(16);
    }

    @Test
    public void testService17() throws Exception {
        verifyServiceBehavior(17);
    }

    @Test
    public void testService18() throws Exception {
        verifyServiceBehavior(18);
    }

    @Test
    public void testService19() throws Exception {
        verifyServiceBehavior(19);
    }

    @Test
    public void testService20() throws Exception {
        verifyServiceBehavior(20);
    }

    @Test
    public void testService22() throws Exception {
        verifyServiceBehavior(22);
    }

    @Test
    public void testService23() throws Exception {
        InternalServerException expected = null;
        try {
            verifyServiceBehavior(23);
        } catch (InternalServerException ex) {
            expected = ex;
        }
        assertNotNull(expected);
    }

    @Test
    public void testService25() throws Exception {
        verifyServiceBehavior(25, POST_SOAP_ACTION);
    }

    @Test
    public void testService26() throws Exception {
        verifyServiceBehavior(26);
    }

    //    TODO add WSDL validation
//    @Test
    // not a valid WSDL test
    public void testService27() throws Exception {
        verifyServiceBehavior(27);
    }

}
