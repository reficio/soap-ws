package com.centeractive.ws.server;

import com.centeractive.ws.builder.utils.ResourceUtils;
import com.centeractive.ws.client.core.SoapClient;
import com.centeractive.ws.server.core.SoapServer;
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
        return ResourceUtils.getResourceWithAbsolutePackagePath("/keystores/1", ".keystore");
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
