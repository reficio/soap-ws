/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.ws.it;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reficio.ws.SoapContext;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.SoapUtils;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.common.ResourceUtils;
import org.reficio.ws.common.XmlUtils;
import org.reficio.ws.it.util.ClientBuilder;
import org.reficio.ws.it.util.TestUtils;
import org.reficio.ws.server.core.SoapServer;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.OperationType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

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

    protected static URL getKeyStoreUrlOne() {
        return ResourceUtils.getResourceWithAbsolutePackagePath("/keystores/single-cert-keystore", ".keystore_1");
    }

    protected static URL getKeyStoreUrlTwo() {
        return ResourceUtils.getResourceWithAbsolutePackagePath("/keystores/single-cert-keystore", ".keystore_2");
    }

    protected static URL getMultiKeyStoreUrl() {
        return ResourceUtils.getResourceWithAbsolutePackagePath("/keystores/multi-cert-keystore", ".keystore");
    }

    protected String getKeyStorePassword() {
        return "changeit";
    }

    protected void verifyServiceBehavior(int testServiceId, ClientBuilder clientBuilder) throws Exception {
        verifyServiceBehavior(testServiceId, null, clientBuilder);
    }

    protected void verifyServiceBehavior(int testServiceId) throws Exception {
        verifyServiceBehavior(testServiceId, new ClientBuilderImpl());
    }

    protected void verifyServiceBehavior(int testServiceId, Boolean postSoapAction, ClientBuilder clientBuilder) throws Exception {
        log.info(String.format("------------------- TESTING SERVICE [%d] -----------------------", testServiceId));
        Wsdl parser = TestUtils.createParserForService(testServiceId);
        registerHandler(server, testServiceId, parser);

        boolean a = false;
        for (QName bindingName : parser.getBindings()) {
            SoapBuilder builder = parser.binding().name(bindingName).find();
            String contextPath = TestUtils.formatContextPath(testServiceId, builder.getBindingName());
            String endpointUrl = formatEndpointAddress(contextPath);

            for (SoapOperation operation : builder.getOperations()) {
                if (postSoapAction == null) {
                    // test both with and without soap action
                    testOperation(clientBuilder, builder, operation, endpointUrl, Boolean.TRUE);
                    testOperation(clientBuilder, builder, operation, endpointUrl, Boolean.FALSE);
                } else {
                    testOperation(clientBuilder, builder, operation, endpointUrl, postSoapAction);
                }
            }
        }
    }

    protected void verifyServiceBehavior(int testServiceId, Boolean postSoapAction) throws Exception {
        verifyServiceBehavior(testServiceId, postSoapAction, new ClientBuilderImpl());
    }

    private void testOperation(ClientBuilder clientBuilder, SoapBuilder soapBuilder, SoapOperation operation, String endpointUrl, Boolean postSoapAction) throws Exception {
        log.info("Testing operation: " + operation);
        String request = soapBuilder.buildInputMessage(operation);
        assertTrue("Generated request is empty!", request.length() > 0);

        Binding binding = soapBuilder.getBinding();
        BindingOperation op = binding.getBindingOperation(operation.getOperationName(), operation.getOperationInputName(),
                operation.getOperationOutputName());

        String response;
        SoapClient client = clientBuilder.buildClient(endpointUrl);
        if (postSoapAction.booleanValue()) {
            String soapAction = SoapUtils.getSOAPActionUri(op);
            response = postRequest(client, request, soapAction);
        } else {
            response = postRequest(client, request);
        }

        SoapContext context = SoapContext.builder().exampleContent(false).build();
        if (op.getOperation().getStyle().equals(OperationType.REQUEST_RESPONSE)) {
            String expectedResponse = soapBuilder.buildOutputMessage(operation, context);
            assertTrue("Generated expectedResponse is empty!", expectedResponse.length() > 0);
            boolean identical = XmlUtils.isIdenticalNormalizedWithoutValues(expectedResponse, response);
            assertTrue("Error during validation of service " + endpointUrl, identical);
        }
    }

    private void registerHandler(SoapServer server, int testServiceId, Wsdl parser) throws WSDLException {
        TestUtils.registerService(server, testServiceId, parser);
    }

    private String formatEndpointAddress(String contextPath) {
        return String.format("%s:%s%s", HOST_URL, HOST_PORT, contextPath);
    }

    private String postRequest(SoapClient client, String request) {
        return client.post(request);
    }

    private String postRequest(SoapClient client, String request, String soapAction) {
        return client.post(soapAction, request);
    }

    class ClientBuilderImpl implements ClientBuilder {
        @Override
        public SoapClient buildClient(String endpointUrl) {
            return SoapClient.builder().endpointUri("http://" + endpointUrl).build();
        }
    }

    protected KeyStore readKeyStore(URL keyStoreUrl, String keyStorePassword, String keyStoreType) {
        InputStream in = null;
        try {
            in = keyStoreUrl.openStream();
            KeyStore ks = KeyStore.getInstance(keyStoreType);
            ks.load(in, keyStorePassword.toCharArray());
            return ks;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
