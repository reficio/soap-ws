package com.centeractive.ws.server;

import com.centeractive.ws.client.SoapClient;
import com.centeractive.ws.client.ex.SoapException;
import com.centeractive.ws.client.ex.SoapTransmissionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 * <p/>
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 19/11/11
 * Time: 6:17 PM
 */
public class PlainCooperationTest extends AbstractCooperationTest {

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
        return client.post(soapAction, request);
    }

//    @Test
//    public void testServices() throws Exception {
//        for (int serviceId = 1; serviceId <= 22; serviceId++) {
//            // verifyServiceBehavior(serviceId);
//
//            String testMethod = " @Test\n" +
//                    "    public void testService%d() throws Exception {\n" +
//                    "        verifyServiceBehavior(%d);\n" +
//                    "    }";
//
//            System.out.println(String.format(testMethod, serviceId, serviceId));
//        }
//    }

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
    public void testService15() throws Exception {
        verifyServiceBehavior(15);
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
    public void testService21() throws Exception {
        verifyServiceBehavior(21);
    }

    @Test
    public void testService22() throws Exception {
        verifyServiceBehavior(22);
    }

    @Test
    public void testService23() throws Exception {
        SoapTransmissionException expected = null;
        try {
            verifyServiceBehavior(23);
        } catch (SoapTransmissionException ex) {
            ex.printStackTrace();
            expected = ex;
        }
        assertNotNull(expected);
        assertEquals(expected.getErrorCode(), 500);
    }

    @Test
    public void testService24_noSoapAction_soap11() throws Exception {
        SoapTransmissionException expected = null;
        try {
            verifyServiceBehavior(24, false);
        } catch (SoapTransmissionException ex) {
            ex.printStackTrace();
            expected = ex;
        }
        assertNotNull(expected);
        assertEquals(expected.getErrorCode(), 500);
    }

    @Test
    public void testService24_withSoapAction_soap11() throws Exception {
        verifyServiceBehavior(24, true);
    }

    @Test
    public void testService25() throws Exception {
        verifyServiceBehavior(25, true);
    }


}
