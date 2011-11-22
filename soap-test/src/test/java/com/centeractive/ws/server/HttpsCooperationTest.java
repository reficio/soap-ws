package com.centeractive.ws.server;

import com.centeractive.utils.ResourceUtils;
import com.centeractive.ws.client.SoapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

import java.net.URL;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 19/11/11
 * Time: 6:17 PM
 */
public class HttpsCooperationTest extends AbstractCooperationTest {

    private final static Log log = LogFactory.getLog(HttpsCooperationTest.class);

    protected URL getTestKeyStoreUrl() {
        return ResourceUtils.getResourceWithAbsolutePackagePath(getClass(), "/keystores/1", ".keystore");
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
        SoapClient client = SoapClient.builder()
                .url("https://" + endpointUrl)
                .keyStoreUrl(getTestKeyStoreUrl())
                .keyStorePassword(getTestKeyStorePassword())
                .create();
        return client.post(request);
    }

    @Test
    public void testServices() throws Exception {
        for(int serviceId = 1 ; serviceId <= 2 ; serviceId++) {
            verifyServiceBehavior(serviceId);
        }
    }

}
