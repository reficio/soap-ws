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
package com.centeractive.ws.server.util;

import com.centeractive.ws.builder.core.SoapBuilder;
import com.centeractive.ws.builder.core.SoapParser;
import com.centeractive.ws.builder.soap.SoapBuilderLegacy;
import com.centeractive.ws.builder.core.SoapContext;
import com.centeractive.ws.builder.utils.ResourceUtils;
import com.centeractive.ws.server.core.SoapServer;
import com.centeractive.ws.server.responder.AutoResponder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.wsdl.Binding;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Collection;

/**
 * Utils used in SoapClient<->Soap Server integration testing
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class TestUtils {

    private final static Log log = LogFactory.getLog(TestUtils.class);

    public static SoapParser createParserForService(int testServiceId) throws WSDLException {
        String path = getTestServiceFolderPath(testServiceId);
        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath(path, "TestService.wsdl");
        SoapParser parser = new SoapParser(wsdlUrl);
        return parser;
    }

    public static String formatContextPath(int testServiceId, QName bindingName) {
        return "/service" + formatServiceId(testServiceId) + "_" + bindingName.getLocalPart();
    }

    public static String getTestServiceFolderPath(int testServiceId) {
        String testServiceIdString = formatServiceId(testServiceId);
        return "/services/test" + testServiceIdString;
    }

    public static String formatServiceId(int testServiceId) {
        return (testServiceId < 10) ? "0" + testServiceId : "" + testServiceId;
    }

    public static void registerService(SoapServer server, int testServiceId) throws WSDLException {
        SoapParser parser = TestUtils.createParserForService(testServiceId);
        registerAutoResponderForAllServiceBindings(server, testServiceId, parser);
    }

    public static void registerService(SoapServer server, int testServiceId, SoapParser parser) throws WSDLException {
        registerAutoResponderForAllServiceBindings(server, testServiceId, parser);
    }

    public static void registerAutoResponderForAllServiceBindings(SoapServer server, int testServiceId, SoapParser parser) {
        for (QName bindingName : parser.getBindings()) {
            String contextPath = TestUtils.formatContextPath(testServiceId, bindingName);
            log.info(String.format("Registering auto responder for service [%d] undex context path [%s]", testServiceId, contextPath));
            SoapContext context = SoapContext.builder().exampleContent(false).build();
            SoapBuilder builder = parser.getBuilder(bindingName);
            server.registerRequestResponder(contextPath, new AutoResponder(builder, context));
        }
    }

    }
