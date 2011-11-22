package com.centeractive.ws.server;

import com.centeractive.SoapBuilder;
import com.centeractive.soap.domain.OperationWrapper;
import com.centeractive.utils.XmlTestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Test;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.OperationType;
import javax.wsdl.WSDLException;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
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
        log.info(String.format("------------------- TESTING SERVICE [%d] -----------------------", testServiceId));
        String url = getUrlString();
        SoapBuilder builder = TestUtils.createBuilderForService(testServiceId);
        registerHandler(testServiceId, builder);
        assertNotNull(builder);
        for (Binding binding : (Collection<Binding>) builder.getDefinition().getAllBindings().values()) {
            for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
                OperationWrapper wrapper = builder.getOperation(binding, operation);
                log.info("Testing operation: " + wrapper);
                String request = builder.buildSoapMessageFromInput(wrapper);
                String contextPath = TestUtils.formatContextPath(testServiceId, binding);
                String endpointUrl = formatEndpointAddress(url, contextPath);
                String response = postRequest(endpointUrl, request);

                if(operation.getOperation().getStyle().equals(OperationType.REQUEST_RESPONSE)) {
                    String expectedResponse = builder.buildSoapMessageFromOutput(builder.getOperation(binding, operation));
                    boolean identical = XmlTestUtils.isIdenticalNormalizedWithoutValues(expectedResponse, response);
                    assertTrue("Error during validation of service " + testServiceId, identical);
                }
            }
        }
        log.info("------------------------------------------------------------------------");
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

}
