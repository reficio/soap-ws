package com.centeractive.ws.server;

import com.centeractive.SoapBuilder;
import com.centeractive.soap.domain.OperationWrapper;
import com.centeractive.utils.XmlTestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.OperationType;
import javax.wsdl.WSDLException;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 * <p/>
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 19/11/11
 * Time: 6:32 PM
 */
public abstract class AbstractCooperationTest {

    private final static Log log = LogFactory.getLog(AbstractCooperationTest.class);

    public static final String HOST_URL = "localhost";
    public static final int HOST_PORT = 9696;

    protected SoapServer server;

    protected void verifyServiceBehavior(int testServiceId) throws Exception {
        verifyServiceBehavior(testServiceId, null);
    }

    protected void verifyServiceBehavior(int testServiceId, Boolean postSoapAction) throws Exception {
        log.info(String.format("------------------- TESTING SERVICE [%d] -----------------------", testServiceId));
        String url = getUrlString();
        SoapBuilder builder = TestUtils.createBuilderForService(testServiceId);
        registerHandler(testServiceId, builder);
        assertNotNull(builder);
        for (Binding binding : (Collection<Binding>) builder.getDefinition().getAllBindings().values()) {
            for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
                if(postSoapAction == null) {
                    // test both with and without soap action
                    testOperation(builder, binding, operation, url, testServiceId, Boolean.TRUE);
                    testOperation(builder, binding, operation, url, testServiceId, Boolean.FALSE);
                } else {
                    testOperation(builder, binding, operation, url, testServiceId, postSoapAction);
                }
            }
        }
        log.info("------------------------------------------------------------------------");
    }


    private void testOperation(SoapBuilder builder, Binding binding, BindingOperation operation, String url,
                                 int testServiceId, Boolean postSoapAction) throws Exception {
        OperationWrapper wrapper = builder.getOperation(binding, operation);
        log.info("Testing operation: " + wrapper);
        String request = builder.buildSoapMessageFromInput(wrapper);
        String contextPath = TestUtils.formatContextPath(testServiceId, binding);
        String endpointUrl = formatEndpointAddress(url, contextPath);

        String response = null;
        if (postSoapAction.booleanValue()) {
            String soapAction = SoapBuilder.getSOAPActionUri(operation);
            response = postRequest(endpointUrl, request, soapAction);
        } else {
            response = postRequest(endpointUrl, request);
        }

        if (operation.getOperation().getStyle().equals(OperationType.REQUEST_RESPONSE)) {
            String expectedResponse = builder.buildSoapMessageFromOutput(builder.getOperation(binding, operation));
            // log.info("Expecting response:\n" + expectedResponse);
            boolean identical = XmlTestUtils.isIdenticalNormalizedWithoutValues(expectedResponse, response);
            assertTrue("Error during validation of service " + testServiceId, identical);
        }
    }

    protected void registerHandler(int testServiceId, SoapBuilder builder) throws WSDLException {
        TestUtils.registerService(server, testServiceId, builder);
    }

    protected String getUrlString() {
        return String.format("%s:%s", HOST_URL, HOST_PORT);
    }

    protected String formatEndpointAddress(String urlString, String contextPath) {
        return String.format("%s%s", urlString, contextPath);
    }

    protected abstract String postRequest(String endpointUrl, String request);

    protected abstract String postRequest(String endpointUrl, String request, String soapAction);

}
