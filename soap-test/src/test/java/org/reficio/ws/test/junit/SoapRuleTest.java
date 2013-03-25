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
package org.reficio.ws.test.junit;

import org.apache.log4j.Logger;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.common.ResourceUtils;

// @Server annotation spawns an instance of a SoapServer for the lifespan of the test / method.
// The SOAP server provides a SOAP auto-responder for the specified binding -> messages are generated and send automatically.
// Generated messages are compliant with the WSDL and the schema (including enumerations, etc.)
// The @Server annotation may be also used to annotate a method -> it spawns a SoapServer for the lifespan of the test method.
// In order to enable the @Server annotation a junit @Rule has to be defined (JUnit requirement):
//   - org.junit.ClassRule in order to enable the @Server on a per-class basis
//   - org.junit.Rule in order to enable the @Server on a per-class basis
@Server(wsdl = "classpath:wsdl/currency-convertor.wsdl", binding = "CurrencyConvertorSoap")
public class SoapRuleTest {

    private final static Logger log = Logger.getLogger(SoapRuleTest.class);
    private static final String WSDL = ResourceUtils.getResource("wsdl/currency-convertor.wsdl").toString();

    @Rule
    // define an instance rule if the @Server annotation is used per method
    public SoapRule rule = new SoapRule();

    @ClassRule
    // define a class rule if the @Server annotation is used per class
    public static SoapRule classRule = new SoapRule();

    @Test
    @Server(wsdl = "classpath:wsdl/currency-convertor.wsdl", binding = "CurrencyConvertorSoap", port = 41414)
    public void testSoapMock_perMethodServer() {
        SoapClient client = SoapClient.builder().endpointUri("http://localhost:41414/service").build();
        SoapBuilder builder = Wsdl.parse(WSDL).binding().localPart("CurrencyConvertorSoap").find();
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        String request = builder.buildInputMessage(operation);

        log.info("\n" + request);

        String response = client.post(request);
        log.info("\n" + response);

    }

    @Test
    public void testSoapMock_perClassServer() {
        SoapClient client = SoapClient.builder().endpointUri("http://localhost:51515/service").build();
        SoapBuilder builder = Wsdl.parse(WSDL).binding().localPart("CurrencyConvertorSoap").find();
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        String request = builder.buildInputMessage(operation);

        log.info("\n" + request);

        String response = client.post(request);
        log.info("\n" + response);

    }

}
