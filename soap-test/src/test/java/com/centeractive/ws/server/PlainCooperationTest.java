package com.centeractive.ws.server;

import com.centeractive.ws.client.SoapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
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
        SoapClient client = SoapClient.builder().url("http://"+endpointUrl).create();
        return client.post(request);
    }

    @Test
    public void testServices() throws Exception {
        for(int serviceId = 1 ; serviceId <= 21 ; serviceId++) {
            verifyServiceBehavior(serviceId);
        }
    }

}
