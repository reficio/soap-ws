package com.centeractive.ws.client;

import com.centeractive.ws.client.ex.SoapTransmissionException;
import org.junit.Test;

import java.net.SocketTimeoutException;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 5/30/12
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleClientTest {

    @Test(timeout = 5000, expected = SoapTransmissionException.class)
    public void connectTimeout() {
        try {
            SoapClient client = SoapClient.builder()
                    .url("http://test.ch:9999")
                    .connectTimeoutInMillis(1000)
                    .create();
            client.post("<xml/>");
        } catch (SoapTransmissionException ex) {
            assertTrue(ex.getCause() instanceof SocketTimeoutException);
            throw ex;
        }
    }

}
