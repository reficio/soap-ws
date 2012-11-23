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
package com.centeractive.ws.builder;

import com.centeractive.ws.SoapContext;
import com.centeractive.ws.builder.core.WsdlParser;
import com.centeractive.ws.common.ResourceUtils;
import com.centeractive.ws.common.XmlUtils;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static junit.framework.Assert.assertTrue;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 12/10/11
 * Time: 12:23 PM
 */
public class ServiceComplianceTest {

    private final static Logger log = Logger.getLogger(ServiceComplianceTest.class);

    enum MessageType {REQUEST, RESPONSE}

    public static String getTestServiceFolderPath(int testServiceId) {
        String testServiceIdString = (testServiceId < 10) ? "0" + testServiceId : "" + testServiceId;
        return "/services/test" + testServiceIdString;
    }

    public static URL getDefinitionUrl(int testServiceId) {
        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath(
                getTestServiceFolderPath(testServiceId), "TestService.wsdl");
        return wsdlUrl;
    }

    public static Definition getDefinition(URL wsdlUrl) throws WSDLException {
        WSDLReader reader = new WSDLReaderImpl();
        Definition definition = reader.readWSDL(wsdlUrl.getPath());
        return definition;
    }

    public static String getExpectedMessage(int testServiceId, String bindingName, String operationName, MessageType msg) {
        String serviceFolderPath = getTestServiceFolderPath(testServiceId);
        String messageFolderPath = String.format("%s/operations/%s", serviceFolderPath, bindingName);
        String fileName = operationName + "." + (MessageType.REQUEST.equals(msg) ? "request" : "response") + ".xml";
        URL fileUrl = ResourceUtils.getResourceWithAbsolutePackagePath(messageFolderPath, fileName);
        File file = null;
        try {
            file = new File(fileUrl.toURI());
        } catch (URISyntaxException e) {
            file = new File(fileUrl.getPath());
        }
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getExpectedRequest(int testServiceId, String bindingName, String operationName) {
        return getExpectedMessage(testServiceId, bindingName, operationName, MessageType.REQUEST);
    }

    public static String getExpectedResponse(int testServiceId, String bindingName, String operationName) {
        return getExpectedMessage(testServiceId, bindingName, operationName, MessageType.RESPONSE);
    }


    @SuppressWarnings("unchecked")
    private static void testService(int testServiceId) throws Exception {
        URL wsdlUrl = getDefinitionUrl(testServiceId);
        WsdlParser parser = WsdlParser.parse(wsdlUrl);
        SoapContext context = SoapContext.builder()
                .exampleContent(false)
                .build();
        for (QName bindingQName : parser.getBindings()) {
            String bindingName = bindingQName.getLocalPart();
            SoapBuilder builder = parser.binding(bindingQName).builder();
            for (SoapOperation operation : builder.getOperations()) {
                String request = builder.buildInputMessage(operation);
                String expectedRequest = getExpectedRequest(testServiceId, bindingName, operation.getOperationName());
                log.info(String.format("Comparing binding=[%s] operation=[%s]", bindingName, operation.getOperationName()));
                log.info("REQUEST:\n" + request);
                log.info("EXPECTED_REQUEST:\n" + expectedRequest);

                request = XmlUtils.normalizeAndRemoveValues(request);
                expectedRequest = XmlUtils.normalizeAndRemoveValues(expectedRequest);
                log.info("REQUEST_NO_VALUES:\n" + request);
                log.info("EXPECTED_REQUEST_NO_VALUES:\n" + expectedRequest);
                assertTrue(XMLUnit.compareXML(expectedRequest, request).identical());

                String response = builder.buildOutputMessage(operation, context);
                String expectedResponse = getExpectedResponse(testServiceId, bindingName, operation.getOperationName());
                log.info("RESPONSE:\n" + response);
                log.info("EXPECTED_RESPONSE:\n" + expectedResponse);

                response = XmlUtils.normalizeAndRemoveValues(response);
                expectedResponse = XmlUtils.normalizeAndRemoveValues(expectedResponse);

                log.info("RESPONSE_NO_VALUES:\n" + response);
                log.info("EXPECTED_RESPONSE_NO_VALUES:\n" + expectedResponse);
                assertTrue(XMLUnit.compareXML(expectedResponse, response).identical());
            }
        }
    }

    @Test
    public void testService01() throws Exception {
        testService(1);
    }

    @Test
    public void testService02() throws Exception {
        testService(2);
    }

    @Test
    public void testService03() throws Exception {
        testService(3);
    }

    @Test
    public void testService04() throws Exception {
        testService(4);
    }

    @Test
    public void testService05() throws Exception {
        testService(5);
    }

    @Test
    public void testService06() throws Exception {
        testService(6);
    }

    @Test
    public void testService07() throws Exception {
        testService(7);
    }

    @Test
    public void testService08() throws Exception {
        testService(8);
    }

    @Test
    public void testService09() throws Exception {
        testService(9);
    }

    @Test
    public void testService10() throws Exception {
        testService(10);
    }

    @Test
    public void testService11() throws Exception {
        testService(11);
    }

    @Test
    public void testService12() throws Exception {
        testService(12);
    }

    @Test
    public void testService13() throws Exception {
        testService(13);
    }

    @Test
    public void testService14() throws Exception {
        testService(14);
    }

    @Test
    public void testService15() throws Exception {
        testService(15);
    }

    @Test
    public void testService16() throws Exception {
        testService(16);
    }

    @Test
    public void testService17() throws Exception {
        testService(17);
    }

    @Test
    public void testService18() throws Exception {
        testService(18);
    }

}
