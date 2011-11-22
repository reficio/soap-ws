package com.centeractive.ws.server;

import com.centeractive.ws.client.SoapClient;
import com.centeractive.ws.client.ex.SoapTransmissionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
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
        SoapClient client = SoapClient.builder().url("http://" + endpointUrl).create();
        try {
            String response = null;
            for (int i = 0; i < 30; i++) {
                response = client.post(request);
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