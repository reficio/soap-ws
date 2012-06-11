/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
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
package com.centeractive.ws.server;

import com.centeractive.ws.builder.core.SoapBuilder;
import com.centeractive.ws.builder.soap.domain.OperationWrapper;
import com.centeractive.ws.builder.utils.XmlTestUtils;
import com.centeractive.ws.server.core.SoapServer;
import com.centeractive.ws.server.util.TestUtils;
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
 * Abstract SoapServer<->SoapClient integration test with lots of convenience methods.
 * Basically, what it does is:
 * - read the test set from the resources folder -> test set id is passed to the reader
 * - starts the soap server and registers an auto-responder for the specified WSDL
 * - generates an XML request and response for every operation in the WSDL
 * - creates a SoapClient and post the XML request to the Server
 * - receives the response from the server and compares it to the response generated locally by the client.
 *
 * @author Tom Bujok
 * @since 1.0.0
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
