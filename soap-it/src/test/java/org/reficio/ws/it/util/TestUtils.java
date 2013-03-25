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
package org.reficio.ws.it.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reficio.ws.SoapContext;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.common.ResourceUtils;
import org.reficio.ws.server.core.SoapServer;
import org.reficio.ws.server.responder.AutoResponder;

import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import java.net.URL;

import static junit.framework.Assert.assertNotNull;

/**
 * Utils used in SoapClient<->Soap Server integration testing
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class TestUtils {

    private final static Log log = LogFactory.getLog(TestUtils.class);

    public static Wsdl createParserForService(int testServiceId) throws WSDLException {
        String path = getTestServiceFolderPath(testServiceId);
        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath(path, "TestService.wsdl");
        Wsdl parser = Wsdl.parse(wsdlUrl);
        assertNotNull(parser);
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
        Wsdl parser = TestUtils.createParserForService(testServiceId);
        registerAutoResponderForAllServiceBindings(server, testServiceId, parser);
    }

    public static void registerService(SoapServer server, int testServiceId, Wsdl parser) throws WSDLException {
        registerAutoResponderForAllServiceBindings(server, testServiceId, parser);
    }

    public static void registerAutoResponderForAllServiceBindings(SoapServer server, int testServiceId, Wsdl parser) {
        for (QName bindingName : parser.getBindings()) {
            String contextPath = TestUtils.formatContextPath(testServiceId, bindingName);
            log.info(String.format("Registering auto responder for service [%d] undex context path [%s]", testServiceId, contextPath));
            SoapContext context = SoapContext.builder().exampleContent(false).build();
            SoapBuilder builder = parser.binding().name(bindingName).find();
            server.registerRequestResponder(contextPath, new AutoResponder(builder, context));
        }
    }

}
