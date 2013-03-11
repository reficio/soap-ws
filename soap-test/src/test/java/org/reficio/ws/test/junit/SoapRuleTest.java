/**
 * Copyright (c) 2012 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.ws.test.junit;

import org.apache.log4j.Logger;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.WsdlParser;
import org.reficio.ws.client.core.SoapClient;

// When a class is annotated with the @Server annotation an instance of the SoapServer is created for the lifespan of the test
// The SOAP server provides a SOAP auto-responder for the specified binding
// The generated responses are compliant with the WSDL and the schema type (including enumerations, etc.)
// @Server annotation may be also used to annotate a method - it creates the SoapServer for the lifespan of the test method.
// In order to enable the server exposition a @Rule or @ClassRule has to be defined (JUnit requirement)
@Server(wsdl = "http://www.webservicex.net/CurrencyConvertor.asmx?WSDL", binding = "CurrencyConvertorSoap")
public class SoapRuleTest {

    private final static Logger log = Logger.getLogger(SoapRuleTest.class);
    private static final String WSDL = "http://www.webservicex.net/CurrencyConvertor.asmx?WSDL";

    // define an instance rule if the @Server is used per method
    @Rule
    public SoapRule rule = new SoapRule();

    // define a class rule if the @Server is used per class
    @ClassRule
    public static SoapRule classRule = new SoapRule();

    @Test
    @Server(wsdl = WSDL, binding = "CurrencyConvertorSoap", port = 41414)
    public void testSoapMock_perMethodServer() {
        SoapClient client = SoapClient.builder().endpointUrl("http://localhost:41414/service").build();
        SoapBuilder builder = WsdlParser.parse(WSDL).binding().localPart("CurrencyConvertorSoap").builder();
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        String request = builder.buildInputMessage(operation);

        log.info("\n" + request);

        String response = client.post(request);
        log.info("\n" + response);

    }

    @Test
    public void testSoapMock_perClassServer() {
        SoapClient client = SoapClient.builder().endpointUrl("http://localhost:51515/service").build();
        SoapBuilder builder = WsdlParser.parse(WSDL).binding().localPart("CurrencyConvertorSoap").builder();
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        String request = builder.buildInputMessage(operation);

        log.info("\n" + request);

        String response = client.post(request);
        log.info("\n" + response);

    }

}
