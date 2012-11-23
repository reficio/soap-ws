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

import com.centeractive.ws.SoapContext;
import com.centeractive.ws.builder.SoapBuilder;
import com.centeractive.ws.builder.SoapOperation;
import com.centeractive.ws.builder.core.WsdlParser;
import com.centeractive.ws.client.core.SoapClient;
import com.centeractive.ws.common.ResourceUtils;
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

import static org.junit.Assert.assertTrue;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapClientExamplesTest {

    private static SoapServer server;
    private static final int port = 9797;
    private static final String contextPath = "/stockquote";

    private static final URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/", "wsdl/stockquote-service.wsdl");
    private static final QName bindingName = new QName("http://centeractive.com/stockquote.wsdl", "StockQuoteSoapBinding");
    private static SoapBuilder builder;

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
        WsdlParser parser = WsdlParser.parse(wsdlUrl);
        builder = parser.binding(bindingName).builder();

        AutoResponder responder = new AutoResponder(builder, context);
        return responder;
    }

    /**
     * Here we're gonna generate the SOAP message using SoapBuilder and post it using SoapClient
     */
    @Test
    public void invoke_tradePriceRequest_generatedMessages() throws Exception, SAXException, WSDLException {
        // construct the client
        String url = String.format("http://localhost:%d%s", port, contextPath);
        SoapClient client = SoapClient.builder()
                .endpointUrl(url)
                .build();

        WsdlParser parser = WsdlParser.parse(wsdlUrl);
        SoapBuilder soapBuilder = parser.binding(bindingName).builder();

        // get the operation to invoked -> assumption our operation is the first operation in the WSDL's
        SoapOperation operation = soapBuilder.operation().name("GetLastTradePrice").find();

        // construct the request
        String request = soapBuilder.buildInputMessage(operation);
        // post the request to the server
        String response = client.post(request);
        // get the response
        String expectedResponse = soapBuilder.buildOutputMessage(operation, SoapContext.NO_CONTENT);

        assertTrue(XMLUnit.compareXML(expectedResponse, response).identical());
    }

    /**
     * Here we're gonna simply post SOAP hardcoded message using SoapClient
     */
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

}
