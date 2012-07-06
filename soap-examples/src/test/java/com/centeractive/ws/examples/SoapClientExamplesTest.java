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
package com.centeractive.ws.examples;

import com.centeractive.ws.builder.core.SoapBuilder;
import com.centeractive.ws.builder.core.SoapContext;
import com.centeractive.ws.builder.soap.domain.OperationWrapper;
import com.centeractive.ws.builder.utils.ResourceUtils;
import com.centeractive.ws.client.core.SoapClient;
import com.centeractive.ws.server.core.SoapServer;
import com.centeractive.ws.server.responder.AutoResponder;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapClientExamplesTest {

    private static SoapServer server;
    private static final int port = 9797;
    private static final String contextPath = "/stockquote";

    private static final URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/", "stockquote-service.wsdl");
    private static final QName bindingName = new QName("http://centeractive.com/stockquote.wsdl", "StockQuoteSoapBinding");


    @BeforeClass
    public static void startServer() throws WSDLException {
        server = SoapServer.builder()
                .httpPort(port)
                .build();
        server.start();

        AutoResponder responder = getAutoResponderForTestService();
        server.registerRequestResponder(contextPath, responder);
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    public static AutoResponder getAutoResponderForTestService() throws WSDLException {
        SoapContext context = SoapContext.builder().exampleContent(false).build();
        AutoResponder responder = new AutoResponder(wsdlUrl, bindingName, context);
        return responder;
    }

    @Test
    public void invoke_tradePriceRequest_hardcodedMessages() throws IOException, SAXException {

        String url = String.format("http://localhost:%d%s", port, contextPath);
        SoapClient client = SoapClient.builder()
                .endpointUrl(url)
                .build();

        String request =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:stoc=\"http://centeractive.com/stockquote.wsdl\" xmlns:stoc1=\"http://centeractive.com/stockquote.xsd\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <stoc:GetLastTradePrice>\n" +
                        "         <stoc1:TradePriceRequest>\n" +
                        "            <tickerSymbol>?</tickerSymbol>\n" +
                        "         </stoc1:TradePriceRequest>\n" +
                        "      </stoc:GetLastTradePrice>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";

        String response = client.post(request);

        String expectedResponse = "" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:stoc=\"http://centeractive.com/stockquote.wsdl\" xmlns:stoc1=\"http://centeractive.com/stockquote.xsd\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <stoc:GetLastTradePriceResponse>\n" +
                "         <stoc1:TradePrice>\n" +
                "            <price>?</price>\n" +
                "         </stoc1:TradePrice>\n" +
                "      </stoc:GetLastTradePriceResponse>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        assertTrue(XMLUnit.compareXML(expectedResponse, response).identical());
    }

    @Test
    public void invoke_tradePriceRequest_generatedMessages() throws Exception, SAXException, WSDLException {

        String url = String.format("http://localhost:%d%s", port, contextPath);
        SoapClient client = SoapClient.builder()
                .endpointUrl(url)
                .build();

        SoapBuilder builder = new SoapBuilder(wsdlUrl);

        // get all bindings
        Set<QName> bindings = builder.getBindingNames();
        // get all operations from binding -> assumption that we take the first binding
        Set<OperationWrapper> ops = builder.getOperationNames(bindings.iterator().next());
        // assumption that we take the first operation
        OperationWrapper op = ops.iterator().next();

        String request = builder.buildSoapMessageFromInput(op);
        String response = client.post(request);
        String expectedResponse = builder.buildSoapMessageFromOutput(op);

        assertTrue(XMLUnit.compareXML(expectedResponse, response).identical());

    }


}
