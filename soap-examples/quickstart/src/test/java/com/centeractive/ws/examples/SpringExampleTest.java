package com.centeractive.ws.examples;

import com.centeractive.ws.SoapContext;
import com.centeractive.ws.builder.SoapBuilder;
import com.centeractive.ws.builder.SoapOperation;
import com.centeractive.ws.client.core.SoapClient;
import com.centeractive.ws.server.core.SoapServer;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringExampleTest {

    private final static Logger log = Logger.getLogger(SpringExampleTest.class);

    @Autowired
    private SoapBuilder builder;

    @Autowired
    private SoapClient client;

    @Autowired
    private SoapServer server;

    @Test
    public void testInjection() {
        assertNotNull(builder);
        assertNotNull(server);
    }

    @Test
    public void testServerStarted() {
        assertTrue(server.isRunning());
    }

    @Test
    public void testRequestResponse() {
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        SoapContext context = SoapContext.builder().exampleContent(true).build();
        String request = builder.buildInputMessage(operation, context);

        String response = client.post(request);
        assertNotNull(response);
    }

}
